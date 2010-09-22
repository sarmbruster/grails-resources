package org.grails.plugin.resource

/**
 * Holder for info about a resource declaration at runtime
 */
class ResourceMeta {
    String sourceUrl
    String minifiedUrl
    String cdnUrl

    String actualUrl
    String contentType
    
    Boolean defer = null // we want tri-state
    
    //@todo impl this later
    //String cachedTagText
    
    File processedFile
    
    // For per-resource options like "minify", 'zip'
    Map attributes = [:]
    
    // For per-resource tag resource attributes like "media", 'width', 'height' etc
    Map tagAttributes = [:]

    Closure prePostWrapper

    // A list of Closures taking request & response. Delegates to resourceMeta
    List requestProcessors = []
    
    boolean exists() {
        processedFile != null
    }
}