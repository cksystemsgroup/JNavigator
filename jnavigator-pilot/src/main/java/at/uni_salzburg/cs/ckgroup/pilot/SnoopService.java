package at.uni_salzburg.cs.ckgroup.pilot;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.cert.CertificateException;
import java.util.Enumeration;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class SnoopService extends DefaultService {
	
    private static final String SPACER = "   ";
    private static final String _EQ_ = " = ";

	public SnoopService (IConfiguration configuraton) {
		super (configuraton);
	}

	public void service(ServletConfig config, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException
	{
		
		final HttpSession session = request.getSession();
        response.setContentType("text/plain");

        PrintWriter out = response.getWriter();

        out.println("Servlet init parameters:");
        Enumeration<?> enumeration = config.getInitParameterNames();
        while (enumeration.hasMoreElements())
        {
                final String key = (String) enumeration.nextElement();
                final String value = config.getInitParameter(key);
                out.println(SPACER + key + _EQ_ + value);
        }
        out.println();

        out.println("Context init parameters:");
        final ServletContext context = config.getServletContext();
        Enumeration<?> params_enum = context.getInitParameterNames();
        while (params_enum.hasMoreElements())
        {
                final String key = (String) params_enum.nextElement();
                final Object value = context.getInitParameter(key);
                out.println(SPACER + key + _EQ_ + value);
        }
        out.println();

        out.println("System properties:");
        params_enum = System.getProperties().keys();
        while (params_enum.hasMoreElements())
        {
                final String key = (String) params_enum.nextElement();
                final Object value = System.getProperty(key);
                out.println(SPACER + key + _EQ_ + value);
        }
        out.println();

        out.println("Context attributes:");
        params_enum = context.getAttributeNames();
        while (params_enum.hasMoreElements())
        {
                final String key = (String) params_enum.nextElement();
                final Object value = context.getAttribute(key);
                out.println(SPACER + key + _EQ_ + value);
        }
        out.println();

        out.println("Request attributes:");
        enumeration = request.getAttributeNames();
        while (enumeration.hasMoreElements())
        {
                final String key = (String) enumeration.nextElement();
                final Object value = request.getAttribute(key);
                out.println(SPACER + key + _EQ_ + value);

                /*
                 * If a request was made over a secure channel, then the servlet can find out with
                 * ServletRequest.isSecure(). Additionally, the container associates some of the
                 * characteristics of the secure channel with ServletRequest attributes available to the
                 * servlet if it calls ServletRequest.getAttribute(). The cipher suite and the algorithm
                 * key size are available as attributes named "javax.servlet.request.cipher-suite" and
                 * "javax.servet.request.key-size" of type String and Integer respectively. If SSL
                 * certificates are associated with the request, they appear as an attribute named
                 * "javax.servlet.request.X509Certificate" of type array of
                 * java.security.cert.X509Certificate. javax.servlet.request.cipher_suite = RC4-MD5
                 * javax.servlet.request.ssl_session =
                 * 4CC91CD51F61BC98C8DCCB8A91135A88AD2FF6EB41F5F6F2DCE970B3640739C8
                 * javax.servlet.request.X509Certificate =
                 * [Ljava.security.cert.X509Certificate;@195c06c1
                 */

                if (!value.getClass().getName().equals("[Ljava.security.cert.X509Certificate;"))
                        continue;

                final java.security.cert.X509Certificate cert_array[] = (java.security.cert.X509Certificate[]) value;
                for (int i = 0; i < cert_array.length; i++)
                {
                        final java.security.cert.X509Certificate cert = cert_array[0];
                        try
                        {
                                cert.checkValidity();
                                out.println(SPACER + i + ": validity  : OK");
                        }
                        catch (CertificateException x)
                        {
                                out.println(SPACER + i + ": validity  : " + x.getMessage());
                        }
                        out.println("      version   : v" + cert.getVersion()); // v3
                        out.println("      serial #  : " + cert.getSerialNumber()); // 2
                        // EmailAddress=webadm@porsche.co.at, CN=Porsche Informatik GmbH, OU="Webserver
                        // Zertifikate",
                        // O=Porsche Informatik GmbH, L=Salzburg, ST=Salzburg, C=AT
                        out.println("      issuerDN  : " + cert.getIssuerDN());
                        // EmailAddress=christian.sitte@porsche.co.at, CN=Christian Sitte, OU=Java,
                        // O=Porsche Informatik GmbH, L=Bergheim, ST=Salzburg, C=AT
                        out.println("      subjectDN : " + cert.getSubjectDN());
                        out.println("      notBefore : " + cert.getNotBefore()); // Fri Oct 11 14:07:18
                        // CEST 2002
                        out.println("      notAfter  : " + cert.getNotAfter()); // Sat Oct 11 14:07:18 CEST
                        // 2003
                        out.println("      sigAlgName: " + cert.getSigAlgName()); // MD5withRSA
                        // out.println (" sigAlgOID : " + cert.getSigAlgOID()); // 1.2.840.113549.1.1.4
                }
        }
        out.println();
        out.println("Servlet Name: " + config.getServletName());
        out.println("Protocol: " + request.getProtocol());
        out.println("Scheme: " + request.getScheme());
        out.println("Server Name: " + request.getServerName());
        out.println("Server Port: " + request.getServerPort());
        out.println("Server Info: " + context.getServerInfo());
        out.println("Remote Addr: " + request.getRemoteAddr());
        out.println("Remote Host: " + request.getRemoteHost());
        out.println("Character Encoding: " + request.getCharacterEncoding());
        out.println("Content Length: " + request.getContentLength());
        out.println("Content Type: " + request.getContentType());
        out.println("Locale: " + request.getLocale());
        out.println("Default Response Buffer: " + response.getBufferSize());
        out.println();
        out.println("Parameter names in this request:");
        enumeration = request.getParameterNames();
        while (enumeration.hasMoreElements())
        {
                final String key = (String) enumeration.nextElement();
                final String[] values = request.getParameterValues(key);
                out.print(SPACER + key + _EQ_);
                for (int i = 0; i < values.length; i++)
                {
                        out.print(values[i] + " ");
                }
                out.println();
        }
        out.println();
        out.println("Headers in this request:");
        enumeration = request.getHeaderNames();
        while (enumeration.hasMoreElements())
        {
                final String key = (String) enumeration.nextElement();
                final String value = request.getHeader(key);
                out.println(SPACER + key + ": " + value);
        }
        out.println();
        out.println("Cookies in this request:");
        final Cookie[] cookies = request.getCookies();
        for (int i = 0; cookies != null && i < cookies.length; i++)
        {
                final Cookie cookie = cookies[i];
                out.println(SPACER + cookie.getName() + _EQ_ + cookie.getValue());
        }
        out.println();

        out.println("Request Is Secure: " + request.isSecure());
        out.println("Auth Type: " + request.getAuthType());
        out.println("HTTP Method: " + request.getMethod());
        out.println("Remote User: " + request.getRemoteUser());
        out.println("Request URI: " + request.getRequestURI());
        out.println("Context Path: " + request.getContextPath());
        out.println("Servlet Path: " + request.getServletPath());
        // out.println("Path Info: " + request.getPathInfo());
        // out.println("Path Trans: " + request.getPathTranslated());
        out.println("Query String: " + request.getQueryString());

        out.println();
        
        out.println("Requested Session Id: " + request.getRequestedSessionId());
        out.println("Current Session Id: " + session.getId());
        out.println("Session Created Time: " + session.getCreationTime());
        out.println("Session Last Accessed Time: " + session.getLastAccessedTime());
        out.println("Session Max Inactive Interval Seconds: " + session.getMaxInactiveInterval());
        out.println();
        out.println("Session values: ");
        final Enumeration<?> names = session.getAttributeNames();
        while (names.hasMoreElements())
        {
                final String name = (String) names.nextElement();
                out.println(SPACER + name + _EQ_ + session.getAttribute(name));
        }
    }

}
