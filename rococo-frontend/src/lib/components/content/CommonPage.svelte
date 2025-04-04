<script lang="ts">
    import ContentPageHeading from "$lib/components/ContentPageHeading.svelte";
    import ListWrapper from "$lib/components/ListWrapper.svelte";
    import {getModalStore} from "@skeletonlabs/skeleton";
    import {checkDuplicates} from "$lib/helpers/dataUtils.js";
    import {prepareModal} from "$lib/helpers/prepareModal.js";
    import type {Writable} from "svelte/store";
    import type {Pageable} from "$lib/types/Pageable";
    import type {StoreDataType} from "$lib/types/DataType";
    import type {ComponentType} from "svelte";
    import {onMount} from "svelte";

    type Type = $$Generic<{ id: string }>;
    export let errorTrigger: (message: string) => void;
    export let successTrigger: (message: string) => void;
    export let store: Writable<StoreDataType<Type>>;
    export let addButtonName: string;
    export let pageTitle: string;
    export let searchPlaceholder: string;
    export let formComponent: ComponentType;
    export let dataTestId: string;
    export let emptyDataTestId: string;
    export let emptySearchText: string;
    export let emptySearchDescription: string;

    export let emptyStateTitle: string;
    export let successMessage: string;
    export let dataKey: string;

    export let loadFunction: ({page}: { page?: number, search?: string }) => Promise<{
        data?: Pageable<Type[]>,
        error?: string
    }>;

    let isSearchNotEmpty = false;
    const modalStore = getModalStore();

    let currentPage = 0;
    let currentSearch = "";

    const handleError = (error: string) => {
        store.update((prevState) => {
            return {
                ...prevState,
                isLoading: false,
            }
        });
        errorTrigger(error);
    }

    const loadSearch = async (search: string) => {
        currentPage = 0;
        store.update((prevState) => {
            return {
                ...prevState,
                data: [],
                isLoading: true,
            }
        });
        await load({search})
        isSearchNotEmpty = search.length > 0;
    };

    const loadMore = async (search: string) => {
        store.update((prevState) => {
            return {
                ...prevState,
                isLoading: true,
            }
        });
        await load({page: ++currentPage, search: search});
    }

    const load = async ({page, search}: {page?: number, search?: string }) => {
        store.update((prevState) => {
            return {
                ...prevState,
                isLoading: true,
            }
        });
        const response = await loadFunction({page, search});
        if (response.error) {
            handleError(response.error);
            return;
        }
        const resData = response.data;
        if (resData) {
            const newBatch = checkDuplicates<Type>(resData.content, store, $store.ignoreIds);

            store.update((prevState) => {
                const data = search ? resData.content : [
                    ...prevState.data,
                    ...newBatch
                ];
                return {
                    ...prevState,
                    data,
                    noMoreData: resData.page.totalPages
                        ? currentPage === resData.page.totalPages - 1
                        : true,
                    isLoading: false,
                }
            });
        }
    }

    const addCallback = async (result: {
        data?: Type,
        error?: string,
    }) => {
        if (result.error) {
            errorTrigger(result.error);
            return;
        }
        const data = result.data;
        if (data) {
            store.update((prevState) => {
                return {
                    ...prevState,
                    data: [
                        data,
                        ...prevState.data,
                    ],
                    ignoreIds: [...prevState.ignoreIds, data?.id],
                }
            });

            const dataKeyValue = data[dataKey as keyof typeof data]; // Указываем тип для dataKey
            successTrigger(`${successMessage}: ${dataKeyValue}`);
        }
    }

    const clickAddButton = () => {
        const modal = prepareModal({
            ref: formComponent,
            callback: addCallback
        });
        modalStore.trigger(modal);
    }

    onMount(async () => {
        store.set({
            data: [],
            noMoreData: false,  // Изначально данные еще могут быть загружены
            isLoading: true,
            ignoreIds: [],
        });
        await load({page: 0});
    });
</script>

<ContentPageHeading
        title={pageTitle}
        searchPlaceholder={searchPlaceholder}
        addButtonName={addButtonName}
        onAddButtonClick={clickAddButton}
        loadFunction={loadSearch}
/>
<ListWrapper data={$store.data}
             {isSearchNotEmpty}
             {dataTestId}
             {emptyDataTestId}
             {emptySearchText}
             {emptySearchDescription}
             {emptyStateTitle}
             noMoreData={$store.noMoreData}
             isLoading={$store.isLoading}
             {loadMore}
             search={currentSearch}
>
    <slot/>
</ListWrapper>