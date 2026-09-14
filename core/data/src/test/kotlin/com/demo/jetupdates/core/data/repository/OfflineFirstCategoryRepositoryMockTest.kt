/*
 * Copyright 2026 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.demo.jetupdates.core.data.repository

import com.demo.jetupdates.core.data.Synchronizer
import com.demo.jetupdates.core.data.model.asEntity
import com.demo.jetupdates.core.data.testdoubles.TestApolloDataSource
import com.demo.jetupdates.core.data.testdoubles.TestCategoryDao
import com.demo.jetupdates.core.database.dao.CategoryDao
import com.demo.jetupdates.core.database.model.CategoryEntity
import com.demo.jetupdates.core.database.model.asExternalModel
import com.demo.jetupdates.core.datastore.AppPreferencesDataSource
import com.demo.jetupdates.core.datastore.UserPreferences
import com.demo.jetupdates.core.datastore.test.InMemoryDataStore
import com.demo.jetupdates.core.model.data.Category
import com.demo.jetupdates.core.network.model.NetworkCategory
import com.demo.jetupdates.core.network.model.NetworkChangeList
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

/**
 * This is clone Category Test class for [OfflineFirstCategoryRepositoryTest] using MockWebServer and Apollo client.
 * Similarly, you can write test for OfflineFirstShopItemRepository  using MockWebServer and Apollo client.
 * @see Google Doc "MockWebServer(okhttp3) and other observations"
 * */
class OfflineFirstCategoryRepositoryMockTest {
    private val testScope = TestScope(UnconfinedTestDispatcher())
    private lateinit var mockWebServer: MockWebServer
    private lateinit var network: TestApolloDataSource
    private lateinit var subject: OfflineFirstCategoriesRepository
    private lateinit var categoryDao: CategoryDao
    private lateinit var appPreferences: AppPreferencesDataSource
    private lateinit var synchronizer: Synchronizer

    @Before
    fun setup() {
        mockWebServer = MockWebServer()

        network = TestApolloDataSource(mockWebServer)
        categoryDao = TestCategoryDao()
        appPreferences =
            AppPreferencesDataSource(InMemoryDataStore(UserPreferences.getDefaultInstance()))
        synchronizer = TestSynchronizer(appPreferences)

        subject = OfflineFirstCategoriesRepository(
            categoryDao = categoryDao,
            network = network,
        )
    }

    @Test
    fun offlineFirstCategoriesRepository_categories_stream_is_backed_by_categories_dao() =
        testScope.runTest {
            mockWebServer.enqueue(
                MockResponse().setBody(
                    """
            {
              "data": {
                "categories": [
                  { 
                    "id": "item_to_delete_id_1", 
                    "updatedAt": "2026-09-10T11:00:00Z", 
                    "isDeleted": false 
                  },
                   { 
                    "id": "item_to_delete_id_2", 
                    "updatedAt": "2026-09-10T11:05:00Z", 
                    "isDeleted": false 
                  },
                   { 
                    "id": "item_to_delete_id_3", 
                    "updatedAt": "2026-09-10T11:10:00Z", 
                    "isDeleted": false 
                  }
                ]
              }
            }
                    """.trimIndent(),
                ).addHeader("Content-Type", "application/json"),
            )

            // 2. Queue response for GetCategoriesByIdsQuery
            mockWebServer.enqueue(
                MockResponse().setBody(
                    """
                {
                  "data": {
                    "categories": [
                      {
                        "id": "item_to_delete_id_1",
                        "name": "Fashion & Apparel",
                        "shortDescription": "Stay Stylish",
                        "longDescription": "Clothing and accessories",
                        "url": "",
                        "imageUrl": "https://example.com/image.svg",
                        "updatedAt": "2026-09-10T11:10:00Z", 
                        "isDeleted": false
                      },
                      {
                        "id": "item_to_delete_id_2",
                        "name": "Electronics & Gadgets",
                        "shortDescription": "Smart Tech for a Smarter Life",
                        "longDescription": "From smartphones to smart homes, explore top-of-the-line electronics and must-have gadgets designed to simplify your life and keep you connected.",
                        "url": "",
                        "imageUrl": "https://example.com/image.svg",
                        "updatedAt": "2026-09-10T11:05:00Z", 
                        "isDeleted": false
                      },
                      {
                        "id": "item_to_delete_id_3",
                        "name": "Home & Living",
                        "shortDescription": "Create Your Perfect Space",
                        "longDescription": "Find everything you need to turn your house into a home — furniture, decor, kitchenware, and more — blending comfort, functionality, and style.",
                        "url": "",
                        "imageUrl": "https://example.com/image.svg",
                        "updatedAt": "2026-09-10T11:10:00Z", 
                        "isDeleted": false
                      }
                    ]
                  }
                }
                    """.trimIndent(),
                ).addHeader("Content-Type", "application/json"),
            )
            subject.syncWith(synchronizer)

            assertEquals(
                categoryDao.getCategoryEntities()
                    .first()
                    .map(CategoryEntity::asExternalModel),
                subject.getCategories()
                    .first(),
            )
        }

    @Test
    fun offlineFirstCategoriesRepository_sync_deletes_items_marked_deleted_on_network_via_mock_server() =
        testScope.runTest {
            mockWebServer.enqueue(
                MockResponse().setBody(
                    """
            {
              "data": {
                "categories": [
                  { 
                    "id": "1", 
                    "updatedAt": "0", 
                    "isDeleted": false 
                  },
                   { 
                    "id": "2", 
                    "updatedAt": "1", 
                    "isDeleted": false 
                  },
                   { 
                    "id": "3", 
                    "updatedAt": "2", 
                    "isDeleted": false 
                  }
                ]
              }
            }
                    """.trimIndent(),
                ).addHeader("Content-Type", "application/json"),
            )

            // 2. Queue response for GetCategoriesByIdsQueryx
            mockWebServer.enqueue(
                MockResponse().setBody(
                    """
                {
                  "data": {
                    "categories": [
                      {
                        "id": "1",
                        "name": "Fashion & Apparel",
                        "shortDescription": "Stay Stylish",
                        "longDescription": "Clothing and accessories",
                        "url": "",
                        "imageUrl": "https://example.com/image.svg",
                        "updatedAt": "0", 
                        "isDeleted": false
                      },
                      {
                        "id": "2",
                        "name": "Electronics & Gadgets",
                        "shortDescription": "Smart Tech for a Smarter Life",
                        "longDescription": "From smartphones to smart homes, explore top-of-the-line electronics and must-have gadgets designed to simplify your life and keep you connected.",
                        "url": "",
                        "imageUrl": "https://example.com/image.svg",
                        "updatedAt": "1", 
                        "isDeleted": false
                      },
                      {
                        "id": "3",
                        "name": "Home & Living",
                        "shortDescription": "Create Your Perfect Space",
                        "longDescription": "Find everything you need to turn your house into a home — furniture, decor, kitchenware, and more — blending comfort, functionality, and style.",
                        "url": "",
                        "imageUrl": "https://example.com/image.svg",
                        "updatedAt": "2", 
                        "isDeleted": false
                      }
                    ]
                  }
                }
                    """.trimIndent(),
                ).addHeader("Content-Type", "application/json"),
            )

            val changeCategories = network.getCategoryChangeList("-1").toMutableList()
            println("jklia ${changeCategories.last()}")

            val networkCategories = network.getCategories()
                .map(NetworkCategory::asEntity)
                .map(CategoryEntity::asExternalModel)

            // Delete half of the items on the network
            val deletedItems = networkCategories
                .map(Category::id)
                .partition { it.chars().sum() % 2 == 0 }
                .first
                .toSet()

            val latestVersion = changeCategories.lastOrNull()?.changeListVersion ?: "0"
            deletedItems.forEach {
                val index = it.toInt()
                val change = NetworkChangeList(
                    id = it,
                    isDelete = true,
                    changeListVersion = "${latestVersion.toInt() + 1}",
                )
                changeCategories[index] = change
            }
            setData()
            subject.syncWith(synchronizer)

            val dbCategories = categoryDao.getCategoryEntities()
                .first()
                .map(CategoryEntity::asExternalModel)
            // Assert that items marked deleted on the network have been deleted locally
            assertEquals(
                networkCategories.map(Category::id) - deletedItems,
                dbCategories.map(Category::id),
            )
            // After sync version should be updated
            assertEquals(
                changeCategories.last().changeListVersion,
                synchronizer.getChangeListVersions().categoryVersion,
            )
        }

    fun setData() {
        /**
         after deleted id 2 , we have updated isDeleted = true, and updatedAt to 3 below
         * old  value
         * {
         * "id": "2",
         * "updatedAt": "2",
         * "isDeleted": false
         * },
         * */

        mockWebServer.enqueue(
            MockResponse().setBody(
                """
            {
              "data": {
                "categories": [
                  { 
                    "id": "1", 
                    "updatedAt": "0", 
                    "isDeleted": false 
                  },
                  
                   { 
                    "id": "3", 
                    "updatedAt": "2", 
                    "isDeleted": false 
                  },
                   { 
                    "id": "2", 
                    "updatedAt": "3", 
                    "isDeleted": true 
                  }
                  
                ]
              }
            }
                """.trimIndent(),
            ).addHeader("Content-Type", "application/json"),
        )

        // 2. Queue response for GetCategoriesByIdsQueryx
        /**  we shouldnt add it as its a response for non deleted ids, called from
         *      modelUpdater = { changedIds ->
         *        val networkCategories = network.getCategories(ids = changedIds)}
         *        in OfflineFirstCategoriesRepository
         * {
         *                "id": "2",
         *               "name": "Electronics & Gadgets",
         *               "shortDescription": "Smart Tech for a Smarter Life",
         *               "longDescription": "From smartphones to smart homes, explore top-of-the-line electronics and must-have gadgets designed to simplify your life and keep you connected.",
         *               "url": "",
         *               "imageUrl": "https://example.com/image.svg",
         *               "updatedAt": "1",
         *               "isDeleted": true
         *             }
         **/
        mockWebServer.enqueue(
            MockResponse().setBody(
                """
                {
                  "data": {
                    "categories": [
                      {
                        "id": "1",
                        "name": "Fashion & Apparel",
                        "shortDescription": "Stay Stylish",
                        "longDescription": "Clothing and accessories",
                        "url": "",
                        "imageUrl": "https://example.com/image.svg",
                        "updatedAt": "0", 
                        "isDeleted": false
                      },
                     
                      {
                        "id": "3",
                        "name": "Home & Living",
                        "shortDescription": "Create Your Perfect Space",
                        "longDescription": "Find everything you need to turn your house into a home — furniture, decor, kitchenware, and more — blending comfort, functionality, and style.",
                        "url": "",
                        "imageUrl": "https://example.com/image.svg",
                        "updatedAt": "2", 
                        "isDeleted": false
                      }
                    ]
                  }
                }
                """.trimIndent(),
            ).addHeader("Content-Type", "application/json"),
        )
    }

    /** Here we do not have to set data repetitively like above test, using MockGraphQLDispatcher below
     * @see Google Doc MockWebServer(okhttp3) and other observations
     * */
    @Test
    fun repository_handles_deletions_via_dynamic_mock_server_optimized() = testScope.runTest {
        val dispatcher = MockGraphQLDispatcher()
        mockWebServer.dispatcher = dispatcher

        // Programmatically simulate a deletion on the server state
        dispatcher.markAsDeleted("2")

        // Execute sync flow cleanly in one go
        subject.syncWith(synchronizer)

        val dbCategories = categoryDao.getCategoryEntities()
            .first()
            .map(CategoryEntity::asExternalModel)

        // Assert that item "2" was successfully deleted locally
        assertEquals(listOf("1", "3"), dbCategories.map(Category::id))

        // Verify version updated correctly
        assertEquals("4", synchronizer.getChangeListVersions().categoryVersion)
    }

    // so we do not have to set data repetitively like above test
    class MockGraphQLDispatcher : Dispatcher() {
        // In-memory state mimicking TestAppNetworkDataSource
        private val categories = mutableMapOf(
            "1" to NetworkCategory(
                id = "1",
                name = "Fashion & Apparel",
                shortDescription = "Stay Stylish",
                longDescription = "Clothing and accessories",
                url = "",
                imageUrl = "https://example.com/image.svg",
                updatedAt = "1",
            ),
            "2" to NetworkCategory(
                id = "2",
                name = "Electronics & Gadgets",
                shortDescription = "Smart Tech",
                longDescription = "Gadgets",
                url = "",
                imageUrl = "https://example.com/image.svg",
                updatedAt = "2",
            ),
            "3" to NetworkCategory(
                id = "3",
                name = "Home & Living",
                shortDescription = "Create Space",
                longDescription = "Home decor",
                url = "",
                imageUrl = "https://example.com/image.svg",
                updatedAt = "3",
            ),
        )

        private val changeLists = mutableMapOf(
            "1" to NetworkChangeList(id = "1", changeListVersion = "1", isDelete = false),
            "2" to NetworkChangeList(id = "2", changeListVersion = "2", isDelete = false),
            "3" to NetworkChangeList(id = "3", changeListVersion = "3", isDelete = false),
        )

        fun markAsDeleted(id: String) {
            val nextVersion =
                (changeLists.values.maxOfOrNull { it.changeListVersion.toInt() } ?: 0) + 1
            // removed from categories , to fetch only 2 remaining items from network.getCategories(ids = changedIds) changeIds = [1,3] agter model deleter
            categories.remove(id)
            changeLists[id] = NetworkChangeList(
                id = id,
                changeListVersion = nextVersion.toString(),
                isDelete = true,
            )
        }

        override fun dispatch(request: RecordedRequest): MockResponse {
            val body = request.body.readUtf8()

            return when {
                body.contains("GetCategoryChangeList") -> {
                    val jsonCategories = changeLists.values
                        .sortedBy { it.changeListVersion.toInt() }
                        .joinToString(",") {
                            """{"id": "${it.id}", "updatedAt": "${it.changeListVersion}", "isDeleted": ${it.isDelete}}"""
                        }
                    MockResponse()
                        .setBody("""{"data": {"categories": [$jsonCategories]}}""")
                        .addHeader("Content-Type", "application/json")
                }

                body.contains("GetCategoriesByIds") -> {
                    // Extract active categories requested by the client
                    val activeCategories = categories.values.joinToString(",") {
                        """{"id": "${it.id}", "name": "${it.name}", "shortDescription": "${it.shortDescription}", "longDescription": "${it.longDescription}", "url": "${it.url}", "imageUrl": "${it.imageUrl}", "updatedAt": "${it.updatedAt}", "isDeleted": ${if (it.id == "2") true else "false"}}"""
                    }

                    MockResponse()
                        .setBody("""{"data": {"categories": [$activeCategories]}}""")
                        .addHeader("Content-Type", "application/json")
                }

                else -> MockResponse().setResponseCode(404)
            }
        }
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }
}
