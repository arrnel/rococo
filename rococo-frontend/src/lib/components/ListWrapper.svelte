<script lang="ts">
    import EmptySearch from "$lib/components/EmptySearch.svelte";
    import EmptyState from "$lib/components/EmptyState.svelte";
    import viewport from "$lib/hooks/useViewport";
    import Loader from "$lib/components/Loader.svelte";

    export let data: any[];
    export let isSearchNotEmpty: boolean;
    export let dataTestId: string;
    export let emptyDataTestId: string;
    export let emptySearchText: string;
    export let emptySearchDescription: string;
    export let emptyStateTitle: string;
    export let noMoreData: boolean;
    export let isLoading: boolean;
    export let loadMore: (search: string) => void;
    export let search: string;

    export let bordered = true;
    export let fullPage = true;

</script>

{#if !data?.length}
    {#if isLoading}
        <Loader/>
    {:else}
        {#if isSearchNotEmpty}
            <EmptySearch
                    dataTestId="{emptyDataTestId}"
                    text={emptySearchText}
                    description={emptySearchDescription}
            />
        {:else}
            <EmptyState
                    dataTestId={emptyDataTestId}
                    text={emptyStateTitle}
                    {bordered}
                    {fullPage}
            />
        {/if}
    {/if}
{:else}
    <div data-testid={dataTestId} class="w-100">
        <slot/>
    </div>
{/if}
{#if !noMoreData}
    <div use:viewport on:viewportenter={() => loadMore(search)}>
        {#if isLoading}
            <Loader/>
        {/if}
    </div>
{/if}