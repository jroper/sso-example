package controllers.security;

import com.onelogin.AccountSettings;
import com.onelogin.saml.Response;
import play.Play;
import play.mvc.Controller;
import play.mvc.Result;

/**
 * The controller for handling ACS redirects
 */
public class SamlController extends Controller {

    private static final String CERTIFICATE = Play.application().configuration().getString("saml.certificate");

    public static Result acs() throws Exception {
        // Read the data from the query String of the redirect
        AccountSettings accountSettings = new AccountSettings();
        accountSettings.setCertificate(CERTIFICATE);

        Response samlResponse = new Response(accountSettings);
        samlResponse.loadXmlFromBase64(request().getQueryString("SAMLResponse"));

        if (samlResponse.isValid()) {
            session(SamlAction.USERNAME_SESSION_KEY, samlResponse.getNameId());
            return redirect(controllers.routes.Application.index());
        } else {
            return ok(views.html.error.render("Response wasn't valid"));
        }

    }
}
