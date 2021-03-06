package org.grails.plugin.resource

/**
 * This mapper is the second phase of CSS rewriting.
 *
 * It finds any "resource:" URIs and then re-relativizes the absolute URI that follows it, using the final
 * locations of all the resources now that all the other CSS-processing mappers have been applied.
 *
 * @see CSSPreprocessor mapper for phase 1 of the process.
 *
 * @author Marc Palmer (marc@grailsrocks.com)
 * @author Luke Daley (ld@ldaley.com)
 */
class CSSRewriterResourceMapper {

    def priority = 1000
    
    def resourceService
    
    static defaultIncludes = ['**/*.css']
    
    /**
     * Find all url() and fix up the url if it is not absolute
     * NOTE: This needs to run after any plugins that move resources around, but before any that obliterate
     * the content i.e. before minify or gzip
     */
    def map(resource, config) {

        def processor = new CSSLinkProcessor()
        processor.process(resource, resourceService) { prefix, originalUrl, suffix ->
            
            if (originalUrl.startsWith('resource:')) {
                def uri = originalUrl - 'resource:'
                
                if (log.debugEnabled) {
                    log.debug "Calculated URI of CSS resource [$originalUrl] as [$uri]"
                }

                // This triggers the processing chain if necessary for any resource referenced by the CSS
                def linkedToResource = resourceService.getResourceMetaForURI(uri, true) { res ->
                    // If there's no decl for the resource, create it with image disposition
                    // otherwise we pop out as a favicon...
                    res.disposition = 'image'
                }

                if (linkedToResource) {
                    if (log.debugEnabled) {
                        log.debug "Calculating URL of ${linkedToResource?.dump()} relative to ${resource.dump()}"
                    }

                    def fixedUrl = linkedToResource.relativeTo(resource)
                    def replacement = "${prefix}${fixedUrl}${suffix}"

                    if (log.debugEnabled) {
                        log.debug "Rewriting CSS URL '${originalUrl}' to '$replacement'"
                    }

                    return replacement
                } else {
                    log.warn "Cannot resolve CSS resource, leaving link as is: ${originalUrl}"
                }
            }
            return "${prefix}${originalUrl}${suffix}"
        }
    }
}