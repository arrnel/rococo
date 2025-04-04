<script lang="ts">

    export let dataTestId: string;
    export let text: string;
    export let bordered = true;
    export let fullPage = true;

</script>

<section data-testid={dataTestId} class={`grid grid-cols-1 ${fullPage ? "p-4" : ""}`}>
    <div
            class={`${fullPage ? "m-20 py-20" : "mb-20 mt-4"} rounded-container-token border-surface-500 text-center`}
            class:border={bordered}>
        <p data-testid="empty-list-default-message" class="mx-4 my-10 text-xl">{text}</p>
    </div>
</section>