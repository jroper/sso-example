package controllers.security;

import com.onelogin.AccountSettings;
import com.onelogin.AppSettings;
import com.onelogin.saml.AuthRequest;
import play.Play;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;

import java.net.URLEncoder;

/**
 * The SAML filter, for authentication
 */
public class SamlAction extends Action<SamlAuthenticated> {

    public static final String USERNAME_SESSION_KEY = "username";
    private static final String SAML_ISSUER = Play.application().configuration().getString("saml.issuer");
    private static final String IDP_SSO_TARGET_URL = Play.application().configuration().getString("saml.idpSsoTargetUrl");

    @Override
    public Result call(Http.Context ctx) throws Throwable {
        // Check if the user is authenticated
        String username = ctx.session().get(USERNAME_SESSION_KEY);

        if (username != null) {
            // Mark the request as authenticated, and then proceed with the request
            ctx.request().setUsername(username);
            return delegate.call(ctx);
        } else {
            // Redirect
            return redirectToIdp(ctx);
        }
    }

    public Result redirectToIdp(Http.Context ctx) throws Exception {
        AppSettings appSettings = new AppSettings();
        // This takes the hostname from the current request.  Alternatively, you may want to
        // load the URL from a config parameter.
        appSettings.setAssertionConsumerServiceUrl(routes.SamlController.acs().absoluteURL(ctx.request()));
        appSettings.setIssuer(SAML_ISSUER);

        AccountSettings accSettings = new AccountSettings();
        accSettings.setIdpSsoTargetUrl(IDP_SSO_TARGET_URL);

        AuthRequest authReq = new AuthRequest(appSettings, accSettings);
        String redirectUrl = IDP_SSO_TARGET_URL + "?SAMLRequest=" +
                AuthRequest.getRidOfCRLF(URLEncoder.encode(authReq.getRequest(AuthRequest.base64), "UTF-8"));
        return redirect(redirectUrl);
    }
}
