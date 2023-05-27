package com.example.picodiploma.storyapp.paginglist

import androidx.paging.PagingData
import com.example.picodiploma.storyapp.api.response.Story

object MockTestUtil {

    fun mockStories(): PagingData<Story> {
        val story1 = Story(
            id = "1",
            name = "Story 1",
            description = "This is Story 1",
            photoUrl = "https://i2-prod.mirror.co.uk/incoming/article28871026.ece/ALTERNATES/s1200c/0_London-at-sunset.jpg",
            createdAt = "2023-05-27T10:15:30",
            lat = 51.509865,
            lon = -0.118092
        )

        val story2 = Story(
            id = "2",
            name = "Story 2",
            description = "This is Story 2",
            photoUrl = "https://www.google.com/url?sa=i&url=https%3A%2F%2Fid.wikipedia.org%2Fwiki%2FParis&psig=AOvVaw2NP0lxZOhmA2blRnpaHpq9&ust=1685247764304000&source=images&cd=vfe&ved=0CBEQjRxqFwoTCND916TTlP8CFQAAAAAdAAAAABAE",
            createdAt = "2023-05-27T11:15:30",
            lat = 48.864716,
            lon = 2.349014
        )

        val story3 = Story(
            id = "3",
            name = "Story 3",
            description = "This is Story 3",
            photoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/7/7a/View_of_Empire_State_Building_from_Rockefeller_Center_New_York_City_dllu_%28cropped%29.jpg/1200px-View_of_Empire_State_Building_from_Rockefeller_Center_New_York_City_dllu_%28cropped%29.jpg",
            createdAt = "2023-05-27T12:15:30",
            lat = 40.712776,
            lon = -74.005974
        )

        return PagingData.from(listOf(story1, story2, story3))
    }


    fun mockEmptyStories(): PagingData<Story> {
        return PagingData.empty()
    }
}
