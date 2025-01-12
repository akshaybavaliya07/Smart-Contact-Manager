//const baseURL = "http://localhost:8080"
const baseURL = "smartcontactapp.ap-south-1.elasticbeanstalk.com"

// Get elements
const sendOtpButton = document.getElementById('sendOtpButton');
const otpSection = document.getElementById('otpSection');
const passwordSection = document.getElementById('passwordSection');
const confirmPasswordSection = document.getElementById('confirmPasswordSection');
const submitButtonSection = document.getElementById('submitButton'); 


// ------------- Handle OTP send button click -------------
sendOtpButton.addEventListener('click', async () => {
    const emailInput = document.getElementById('email').value.trim();

    if (!validateEmail(emailInput)) {
        document.getElementById('email-ErrorMessage').textContent = 'Enter valid email';
    } else {
        const userExists = await validateUserExit(emailInput);

        if (!userExists) {
            document.getElementById('email-ErrorMessage').textContent = 'User not found';
        } else {
            // Remove email error message
            document.getElementById('email-ErrorMessage').textContent = '';
            // Disable the button and change its appearance
            sendOtpButton.disabled = true;
            sendOtpButton.textContent = "OTP Sent";
            sendOtpButton.classList.add('bg-gray-400', 'cursor-not-allowed', 'text-gray-600');
            sendOtpButton.classList.remove('hover:bg-blue-700');

            // Show OTP input and password reset fields
            otpSection.classList.remove('hidden');
            passwordSection.classList.remove('hidden');
            confirmPasswordSection.classList.remove('hidden');
            submitButtonSection.classList.remove('hidden');

            // Send OTP email to the user
            await sendOtpEmail(emailInput);
        }
    }
});

function validateEmail(email) {
    // Check if the email is not blank
    if (email.trim() === "") {
        return false;
    }
    // Regular expression to validate the email format
    const emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    if (!emailPattern.test(email)) {
        return false;
    }
    return true;
}

async function validateUserExit(email) {
    try {
        const response = await fetch(`${baseURL}/api/user/check-existence/${email}`);
        const data = await response.json();
        return data.isUserExists;
    } catch (error) {
        console.error('Error checking user existence:', error);
        return false;
    }
}

async function sendOtpEmail(email) {
    await fetch(`${baseURL}/api/user/send-otp/${email}`);
}

// ------------- Handle form submission -------------
document.querySelector('form').addEventListener('submit', function(event) {
    validateForm(event);
});

async function validateForm(event) {
    event.preventDefault();

    const emailInput = document.getElementById('email').value.trim();
    const passwordInput = document.getElementById('password').value.trim();
    const confirmPasswordInput = document.getElementById('confirmPassword').value.trim();
    const otpInput = document.getElementById('otp').value;

    // First, validate passwords match
    if (!validatePasswords(passwordInput, confirmPasswordInput)) {
        return false;
    }

    // Then validate OTP before submitting the form
    const isOtpValid = await validateOtp(emailInput, otpInput);
    console.log("otpis valid or not => " + isOtpValid);
    if (!isOtpValid) {
        document.getElementById('error-message').textContent = 'Invalid OTP. Please try again.'; // Error message for invalid OTP
        return false;
    }

    // If all validations pass, allow form submission
    document.querySelector('form').submit();
    return true;
}

async function validateOtp(email, otpInput) {
    try {
        const response = await fetch(`${baseURL}/api/user/verify-otp/${otpInput}/${email}`);
        const data = await response.json();
        return data.isValidOtp;
    } catch (error) {
        document.getElementById('error-message').textContent = 'Error verifying OTP. Please try again later.';
        return false;
    }
}

function validatePasswords(password, confirmPassword) {
    if (password !== confirmPassword) {
        document.getElementById('error-message').textContent = "Passwords do not match."; // Error message for password mismatch
        return false; // Prevent form submission if passwords do not match
    }

    return true;
}


// Display error message on page load if there are any validation errors in the form
window.onload = function() {
    if (document.getElementById('errorSection')) {
        formValidationErr();
    }
};

function formValidationErr() {
    // Disable the button and change its appearance
    sendOtpButton.disabled = true;
    sendOtpButton.textContent = "OTP Sent";
    sendOtpButton.classList.add('bg-gray-400', 'cursor-not-allowed', 'text-gray-600');
    sendOtpButton.classList.remove('hover:bg-blue-700');

    // Show OTP input and password reset fields
    otpSection.classList.remove('hidden');
    passwordSection.classList.remove('hidden');
    confirmPasswordSection.classList.remove('hidden');
    submitButtonSection.classList.remove('hidden');
}