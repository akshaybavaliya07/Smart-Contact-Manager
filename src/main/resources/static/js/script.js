// Cache the HTML element
const htmlElement = document.documentElement;
const themeToggleButton = document.querySelector("#theme_change");

// Apply the current theme on page load
applyTheme(getTheme());

// Add event listener to toggle the theme
themeToggleButton.addEventListener("click", toggleTheme);

function toggleTheme() {
    const currentTheme = getTheme();
    const newTheme = currentTheme === "light" ? "dark" : "light";

    // Update the theme on the HTML element
    htmlElement.classList.remove(currentTheme);
    htmlElement.classList.add(newTheme);

    // Persist the new theme
    setTheme(newTheme);
}

function setTheme(theme) {
    localStorage.setItem("theme", theme);
}

function getTheme() {
    return localStorage.getItem("theme") || "light";
}

function applyTheme(theme) {
    htmlElement.classList.add(theme);
}



// ------------ navbar navigation --------------
document.addEventListener("DOMContentLoaded", () => {
    // Get the current path
    const currentPath = window.location.pathname;
    // console.log(currentPath);

    // Select all nav links
    const navLinks = document.querySelectorAll("a.nav-link");

    // Loop through links and match the current path
    navLinks.forEach(link => {
        const href = link.getAttribute("href");
        if (currentPath === href) {
            link.classList.add("text-blue-700");
        } else {
            link.classList.remove("text-blue-700");
        }
    });
});

// -------- removing verification messages from login page -------- 
const successMessage = document.getElementById("success-message");

if (successMessage) {
    setTimeout(function() {
        successMessage.classList.add("hidden");
    }, 3000);
}

// --------- removing disabled messages from login page --------
const disabledMessage = document.getElementById("disabled-message");
