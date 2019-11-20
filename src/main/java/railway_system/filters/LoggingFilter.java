package railway_system.filters;


import org.apache.log4j.Logger;
import org.glassfish.jersey.message.internal.ReaderWriter;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Provider
@Logged
public class LoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {

    @Context
    private HttpServletRequest httpServletRequest;
    private static Logger LOGGER = Logger.getLogger( LoggingFilter.class.getName() );
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        log(requestContext);
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        log(responseContext);
    }

    private void log(ContainerRequestContext context) {
        // implementation goes here
        StringBuilder sb = new StringBuilder();
        sb.append("User: ").append( context.getSecurityContext().getUserPrincipal() == null ? "unknown"
                : context.getSecurityContext().getUserPrincipal());
        sb.append(" - Path: ").append(context.getUriInfo().getPath());
        sb.append(" - Header: ").append(context.getHeaders());
        sb.append(" - Entity: ").append(getEntityBody(context));
        LOGGER.info("HTTP REQUEST : " + sb.toString());
    }

    private void log(ContainerResponseContext context) {
        // implementation goes here
        StringBuilder sb = new StringBuilder();
        sb.append("Header: ").append(context.getHeaders());
        sb.append(" - Entity: ").append(context.getEntity());
        LOGGER.info( "HTTP RESPONSE : " + sb.toString());
    }

    private String getEntityBody(ContainerRequestContext requestContext)
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        InputStream in = requestContext.getEntityStream();

        final StringBuilder b = new StringBuilder();
        try
        {
            ReaderWriter.writeTo(in, out);

            byte[] requestEntity = out.toByteArray();
            if (requestEntity.length == 0)
            {
                b.append("").append("\n");
            }
            else
            {
                b.append(new String(requestEntity)).append("\n");
            }
            requestContext.setEntityStream( new ByteArrayInputStream(requestEntity) );

        } catch (IOException ex) {
            //Handle logging error
            LOGGER.error(ex.getMessage());
        }
        return b.toString();
    }
}