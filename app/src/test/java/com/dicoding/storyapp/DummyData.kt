package com.dicoding.storyapp

import com.dicoding.storyapp.data.model.Story


object DataDummy {
    fun generateDummyStories(): List<Story> {
        val stories = mutableListOf<Story>()
        for (i in 1..10) {
            stories.add(
                Story(
                    id = "story-$i",
                    name = "Story $i",
                    description = "Description $i",
                    photoUrl = "https://example.com/story-$i.jpg",
                    createdAt = "2024-12-20T00:00:00Z",
                    lat = i * 1.0,
                    lon = i * 1.0
                )
            )
        }
        return stories
    }
}