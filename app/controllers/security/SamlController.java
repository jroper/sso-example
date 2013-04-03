package controllers.security;

import play.libs.F;
import play.libs.WS;
import play.mvc.Controller;
import play.mvc.Result;

/**
 * The controller for handling ACS redirects
 */
public class SamlController extends Controller {

    public static Result acs() {
        // Read the data from the query String of the redirect
        // eg request().getQueryString("someparam");

        // Now build the XML payload for validating it
        String someXml = "";

        // Make the call to the IDP
        F.Promise<WS.Response> response = WS.url("http://myexampleidp.com/assertion/path")
                .setContentType("application/xml")
                .post(someXml);

        // Handle the response
        return async(response.map(new F.Function<WS.Response, Result>() {
            @Override
            public Result apply(WS.Response response) throws Throwable {
                return handleResponse(response);
            }
        }));
    }

    private static Result handleResponse(WS.Response response) {
        String body = response.getBody();

        // Pass this body to SAML library to verify
        boolean isValid = true;

        if (isValid) {
            // Find the user name and set it in the session
            String username = "foo";
            session(SamlAction.USERNAME_SESSION_KEY, username);

            // Redirect to index
            return redirect(controllers.routes.Application.index());
        } else {
            // Render error page
            return ok(views.html.error.render("Response wasn't valid"));
        }

    }

}
