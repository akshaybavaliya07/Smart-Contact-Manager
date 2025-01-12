package com.scm.forms;

import jakarta.validation.constraints.Max; 
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class ForgotPasswordForm {

    private String email;

    @Min(value = 100000, message = "OTP must be a 6-digit number")
    @Max(value = 999999, message = "OTP must be a 6-digit number")
    private int otp;

    @NotBlank(message = "password is required")
    @Size(min = 6, message = "Minimum 6 characters are required")
    private String password;

    private String confirmPassword;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getOtp() {
		return otp;
	}

	public void setOtp(int otp) {
		this.otp = otp;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}
}