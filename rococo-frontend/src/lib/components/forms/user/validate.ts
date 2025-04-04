import {Errors} from "$lib/types/Errors";
import {userFormErrorStore} from "$lib/components/forms/user/user-form.error.store";
import {museumFormErrorStore} from "$lib/components/forms/museum/museum-form.error.store";

export const validateForm = (
    firstname: string,
    lastname: string) => {

    userFormErrorStore.update((prevState) => {
        return {
            ...prevState,
            firstname: firstname?.length < 3
                ? Errors.NAME_LENGTH_CONSTRAINT_MIN
                : firstname?.length > 255
                    ? Errors.NAME_LENGTH_CONSTRAINT_MAX
                    : "",

            lastname: lastname?.length < 3
                ? Errors.SURNAME_LENGTH_CONSTRAINT_MIN
                : lastname?.length > 255
                    ? Errors.SURNAME_LENGTH_CONSTRAINT_MAX
                    : "",

        }
    });

}