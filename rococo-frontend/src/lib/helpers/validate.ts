import {Errors} from "$lib/types/Errors";

const MAX_SIZE_MB = 15;
const MAX_SIZE_BYTES = MAX_SIZE_MB * 1024 * 1024;
const ALLOWED_FORMATS = ["image/png", "image/jpeg", "image/jpg"];

export const validateImage = (src: File) => {

    let message = "";
    if (!ALLOWED_FORMATS.includes(src.type)) {
        message = Errors.IMAGE_CONSTRAINT_INVALID_FORMAT;
    }

    if (src.size > MAX_SIZE_BYTES) {
        message = message ? message + ". " + Errors.IMAGE_CONSTRAINT_TOO_BIG: Errors.IMAGE_CONSTRAINT_TOO_BIG;
    }
    return message;

}