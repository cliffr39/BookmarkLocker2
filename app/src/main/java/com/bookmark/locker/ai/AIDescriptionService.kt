package com.bookmark.locker.ai

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URI
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AIDescriptionService @Inject constructor() {
    
    private val generativeModel = GenerativeModel(
        modelName = "gemini-pro",
        apiKey = "YOUR_API_KEY_HERE", // You'll need to add your Gemini API key
        generationConfig = generationConfig {
            temperature = 0.7f
            topK = 16
            topP = 0.95f
            maxOutputTokens = 100
        }
    )
    
    suspend fun generateDescription(url: String, title: String = ""): String {
        return withContext(Dispatchers.IO) {
            try {
                val domain = extractDomain(url)
                val prompt = buildPrompt(url, title, domain)
                
                val response = generativeModel.generateContent(prompt)
                val description = response.text?.trim() ?: generateFallbackDescription(domain, title)
                
                // Clean up the description and ensure it's not too long
                cleanDescription(description)
            } catch (e: Exception) {
                // Fallback to simple domain-based description if AI fails
                generateFallbackDescription(extractDomain(url), title)
            }
        }
    }
    
    private fun buildPrompt(url: String, title: String, domain: String): String {
        return """
            Generate a brief, informative description (1-2 sentences, max 100 characters) for this bookmark:
            URL: $url
            Title: $title
            Domain: $domain
            
            The description should help users understand what this webpage contains. Be concise and factual.
            Focus on the type of content or service the website provides.
        """.trimIndent()
    }
    
    private fun extractDomain(url: String): String {
        return try {
            val uri = URI(url)
            uri.host?.removePrefix("www.") ?: url
        } catch (e: Exception) {
            url.substringAfter("://").substringBefore("/").removePrefix("www.")
        }
    }
    
    private fun generateFallbackDescription(domain: String, title: String): String {
        return when {
            domain.contains("github") -> "Code repository and development platform"
            domain.contains("youtube") -> "Video content and entertainment platform"
            domain.contains("stackoverflow") -> "Programming Q&A and developer community"
            domain.contains("reddit") -> "Social news and discussion platform"
            domain.contains("wikipedia") -> "Online encyclopedia and reference"
            domain.contains("medium") -> "Article and blog publishing platform"
            domain.contains("twitter") || domain.contains("x.com") -> "Social media and news platform"
            domain.contains("linkedin") -> "Professional networking platform"
            domain.contains("amazon") -> "E-commerce and shopping platform"
            domain.contains("google") -> "Search and web services"
            domain.contains("facebook") -> "Social networking platform"
            domain.contains("netflix") -> "Video streaming service"
            domain.contains("spotify") -> "Music streaming platform"
            domain.contains("news") || domain.contains("cnn") || domain.contains("bbc") -> "News and current events"
            title.isNotEmpty() -> "Website: ${title.take(50)}"
            else -> "Website from $domain"
        }
    }
    
    private fun cleanDescription(description: String): String {
        return description
            .replace(Regex("[\"'`]"), "") // Remove quotes
            .replace(Regex("\\s+"), " ") // Normalize whitespace
            .trim()
            .take(150) // Limit length
            .let { if (it.endsWith(".")) it else "$it." } // Ensure it ends with period
    }
    
    suspend fun generateTagSuggestions(url: String, title: String = "", existingTags: List<String> = emptyList()): List<String> {
        return withContext(Dispatchers.IO) {
            android.util.Log.d("AIService", "Generating tag suggestions for URL: $url, Title: $title")
            try {
                val domain = extractDomain(url)
                android.util.Log.d("AIService", "Extracted domain: $domain")
                
                // Always try fallback first since AI service likely doesn't have valid API key
                val fallbackTags = generateFallbackTags(domain, title)
                android.util.Log.d("AIService", "Generated fallback tags: $fallbackTags")
                
                // Filter out existing tags and return top 5
                val filteredTags = fallbackTags
                    .filter { suggested -> existingTags.none { existing -> existing.equals(suggested, ignoreCase = true) } }
                    .take(5)
                
                android.util.Log.d("AIService", "Final filtered tags: $filteredTags")
                return@withContext filteredTags
                
            } catch (e: Exception) {
                android.util.Log.e("AIService", "Error generating tag suggestions", e)
                // Fallback to domain-based tags if anything fails
                val fallbackTags = generateFallbackTags(extractDomain(url), title)
                    .filter { suggested -> existingTags.none { existing -> existing.equals(suggested, ignoreCase = true) } }
                    .take(5)
                android.util.Log.d("AIService", "Fallback tags after error: $fallbackTags")
                return@withContext fallbackTags
            }
        }
    }
    
    private fun buildTagPrompt(url: String, title: String, domain: String, existingTags: List<String>): String {
        val existingTagsText = if (existingTags.isNotEmpty()) {
            "\nExisting tags to avoid: ${existingTags.joinToString(", ")}"
        } else ""
        
        return """
            Generate 3-5 relevant tags for this bookmark. Tags should be:
            - Single words or short phrases (max 2 words)
            - Lowercase
            - Descriptive of the content type, topic, or category
            - Useful for organizing bookmarks
            
            URL: $url
            Title: $title
            Domain: $domain$existingTagsText
            
            Return only the tags, separated by commas, no explanations.
            Example format: programming, tutorial, javascript, frontend, react
        """.trimIndent()
    }
    
    private fun parseTagSuggestions(response: String): List<String> {
        return response
            .split(",")
            .map { it.trim().lowercase() }
            .filter { it.isNotBlank() && it.length <= 20 }
            .distinct()
    }
    
    private fun generateFallbackTags(domain: String, title: String): List<String> {
        val tags = mutableListOf<String>()
        
        // Domain-based tags
        when {
            domain.contains("github") -> tags.addAll(listOf("code", "programming", "repository", "development"))
            domain.contains("youtube") -> tags.addAll(listOf("video", "entertainment", "tutorial", "media"))
            domain.contains("stackoverflow") -> tags.addAll(listOf("programming", "qa", "development", "help"))
            domain.contains("reddit") -> tags.addAll(listOf("social", "discussion", "community", "news"))
            domain.contains("wikipedia") -> tags.addAll(listOf("reference", "encyclopedia", "knowledge", "research"))
            domain.contains("medium") -> tags.addAll(listOf("article", "blog", "writing", "opinion"))
            domain.contains("twitter") || domain.contains("x.com") -> tags.addAll(listOf("social", "news", "updates", "microblog"))
            domain.contains("linkedin") -> tags.addAll(listOf("professional", "networking", "career", "business"))
            domain.contains("amazon") -> tags.addAll(listOf("shopping", "ecommerce", "products", "retail"))
            domain.contains("google") -> tags.addAll(listOf("search", "tools", "services", "productivity"))
            domain.contains("facebook") -> tags.addAll(listOf("social", "networking", "friends", "community"))
            domain.contains("netflix") -> tags.addAll(listOf("streaming", "movies", "tv", "entertainment"))
            domain.contains("spotify") -> tags.addAll(listOf("music", "streaming", "audio", "entertainment"))
            domain.contains("news") || domain.contains("cnn") || domain.contains("bbc") -> tags.addAll(listOf("news", "current events", "media", "journalism"))
            else -> {
                // Try to extract category from domain name
                when {
                    domain.contains("blog") -> tags.addAll(listOf("blog", "writing", "personal"))
                    domain.contains("docs") || domain.contains("documentation") -> tags.addAll(listOf("documentation", "reference", "guide"))
                    domain.contains("tutorial") -> tags.addAll(listOf("tutorial", "learning", "guide"))
                    domain.contains("api") -> tags.addAll(listOf("api", "development", "reference"))
                    domain.contains("forum") -> tags.addAll(listOf("forum", "discussion", "community"))
                    else -> tags.addAll(listOf("website", "resource"))
                }
            }
        }
        
        // Title-based tags (extract common keywords)
        if (title.isNotEmpty()) {
            val titleLower = title.lowercase()
            val keywords = listOf(
                "tutorial" to "tutorial",
                "guide" to "guide", 
                "how to" to "howto",
                "api" to "api",
                "documentation" to "docs",
                "blog" to "blog",
                "news" to "news",
                "review" to "review",
                "tool" to "tools",
                "free" to "free",
                "open source" to "opensource",
                "javascript" to "javascript",
                "python" to "python",
                "react" to "react",
                "vue" to "vue",
                "angular" to "angular",
                "node" to "nodejs",
                "css" to "css",
                "html" to "html",
                "design" to "design",
                "ui" to "ui",
                "ux" to "ux"
            )
            
            keywords.forEach { (keyword, tag) ->
                if (titleLower.contains(keyword)) {
                    tags.add(tag)
                }
            }
        }
        
        return tags.distinct().take(5)
    }
}
