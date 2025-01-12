document.querySelector("#upload_image_input").addEventListener("change", () => {

    const file = document.querySelector("#upload_image_input").files[0];
    const reader = new FileReader();
    reader.onload = () => {
        document.querySelector("#upload_image_preview").setAttribute("src", reader.result);
    };
    reader.readAsDataURL(file);
});