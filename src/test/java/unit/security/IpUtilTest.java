package unit.security;

import com.shaurya.hospitalManagement.security.IpUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("IpUtil Tests")
class IpUtilTest {

    private IpUtil ipUtil;

    @BeforeEach
    void setUp() {
        ipUtil = new IpUtil();
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    @DisplayName("Should return 0.0.0.0 when no request context available")
    void getClientIp_NoRequestContext_ShouldReturnDefault() {
        String ip = ipUtil.getClientIp();
        assertThat(ip).isEqualTo("0.0.0.0");
    }

    @Test
    @DisplayName("Should extract IP from X-Forwarded-For header")
    void getClientIp_WithXForwardedFor_ShouldReturnIp() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Forwarded-For", "192.168.1.1");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        String ip = ipUtil.getClientIp();

        assertThat(ip).isEqualTo("192.168.1.1");
    }

    @Test
    @DisplayName("Should extract IP from X-Real-IP when X-Forwarded-For is unknown")
    void getClientIp_WithXRealIp_ShouldReturnIp() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Forwarded-For", "unknown");
        request.addHeader("X-Real-IP", "192.168.1.2");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        String ip = ipUtil.getClientIp();

        assertThat(ip).isEqualTo("192.168.1.2");
    }

    @Test
    @DisplayName("Should use RemoteAddr when headers are not available")
    void getClientIp_WithRemoteAddr_ShouldReturnIp() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("192.168.1.3");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        String ip = ipUtil.getClientIp();

        assertThat(ip).isEqualTo("192.168.1.3");
    }

    @Test
    @DisplayName("Should extract first IP from comma-separated X-Forwarded-For")
    void getClientIp_WithMultipleForwardedIps_ShouldReturnFirst() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Forwarded-For", "192.168.1.1, 10.0.0.1, 172.16.0.1");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        String ip = ipUtil.getClientIp();

        assertThat(ip).isEqualTo("192.168.1.1");
    }

    @Test
    @DisplayName("Should return 0.0.0.0 when all IP sources are null")
    void getClientIp_WithNoIpSources_ShouldReturnDefault() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr(null);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        String ip = ipUtil.getClientIp();

        assertThat(ip).isEqualTo("0.0.0.0");
    }
}