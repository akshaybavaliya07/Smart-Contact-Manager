package com.scm.services.implementation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.scm.services.OtpService;

@Service
public class OtpServiceImplementation implements OtpService, AutoCloseable {

    private final Map<String, OtpData> otpStore = new ConcurrentHashMap<>();
    private static final long OTP_EXPIRATION_TIME = 5 * 60 * 1000; // 5 minutes in milliseconds

    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    public OtpServiceImplementation() {
        executorService.scheduleAtFixedRate(this::cleanupExpiredOtps, 1, 1, TimeUnit.MINUTES);
    }

    @Override
    public void saveOtp(String email, int otp) {
        otpStore.put(email, new OtpData(otp, System.currentTimeMillis()));
    }

    @Override
    public boolean verifyOtp(String email, int otp) {
        OtpData otpData = otpStore.get(email);
        if (otpData != null && otpData.getOtp() == otp) {
            otpStore.remove(email);
            return true;
        }
        return false;
    }

    private void cleanupExpiredOtps() {
        long currentTime = System.currentTimeMillis();
        otpStore.entrySet().removeIf(entry -> currentTime - entry.getValue().getTimestamp() > OTP_EXPIRATION_TIME);
    }

    private static class OtpData {
        private final int otp;
        private final long timestamp;

        public OtpData(int otp, long timestamp) {
            this.otp = otp;
            this.timestamp = timestamp;
        }

        public int getOtp() {
            return otp;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }

    @Override
    public void close() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(1, TimeUnit.MINUTES)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
