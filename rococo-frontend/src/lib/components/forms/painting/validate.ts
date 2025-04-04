import {Errors} from "$lib/types/Errors";
import {paintingFormErrorStore} from "$lib/components/forms/painting/painting-form.error.store";

export const validateForm = (
    title: string,
    description: string,
    authorId: string) => {

    paintingFormErrorStore.update((prevState) => {
        return {
            ...prevState,
            title: title?.length < 3
                ? Errors.TITLE_LENGTH_CONSTRAINT_MIN
                : title?.length > 255
                    ? Errors.TITLE_LENGTH_CONSTRAINT_MAX
                    : "",

            description: description?.length < 10
                ? Errors.DESCRIPTION_LENGTH_CONSTRAINT_MIN
                : description?.length > 2000
                    ? Errors.DESCRIPTION_LENGTH_CONSTRAINT_MAX
                    : "",

            authorId: !(authorId) ? Errors.AUTHOR_CONSTRAINT_NOT_EMPTY : "",
        }
    });
}