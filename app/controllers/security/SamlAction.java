package controllers.security;

import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;

/**
 * The SAML filter, for authentication
 */
public class SamlAction extends Action<SamlAuthenticated> {

    public static final String USERNAME_SESSION_KEY = "username";

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
            return redirectToIdp();
        }
    }

    public Result redirectToIdp() {
        // At this point we need to build a SAML authentication request, probably using the OpenSAML libraries

        // Part of this authentication request will include a URL to redirect back to
        String returnUrl = routes.SamlController.acs().url();

        String redirectUrl = "http://myexampleidp.com/authentication/request?returnUrl=" + returnUrl;

        return redirect(redirectUrl);
    }
}
