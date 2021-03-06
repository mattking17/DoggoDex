package me.mking.doggodex

import com.google.common.truth.Truth
import io.mockk.*
import kotlinx.coroutines.runBlocking
import me.mking.doggodex.data.mappers.DogBreedImagesResponseMapper
import me.mking.doggodex.data.mappers.DogBreedListResponseMapper
import me.mking.doggodex.data.models.DogBreedImagesResponse
import me.mking.doggodex.data.models.DogBreedListResponse
import me.mking.doggodex.data.repositories.DogBreedRepositoryImpl
import me.mking.doggodex.data.repositories.DogBreedsServiceException
import me.mking.doggodex.data.services.DogBreedsService
import me.mking.doggodex.domain.entities.DogBreedEntity
import me.mking.doggodex.domain.repositories.DogBreedRepository
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class DogBreedRepositoryImplTest {

    private companion object {
        val SOME_DOG_BREED_LIST_RESPONSE = DogBreedListResponse(
            message = mapOf(
                "australian" to listOf("shepherd"),
                "bulldog" to listOf("boston", "english", "french"),
                "pug" to emptyList()
            ),
            status = "success"
        )
        val SOME_DOG_BREED_IMAGES_RESPONSE = DogBreedImagesResponse(
            message = listOf(
                "https://images.dog.ceo/breeds/hound-afghan/n02088094_13909.jpg",
                "https://images.dog.ceo/breeds/hound-afghan/n02088094_2522.jpg",
                "https://images.dog.ceo/breeds/hound-afghan/n02088094_5521.jpg"
            ),
            status = "success"
        )
        val SOME_DOG_BREED_ENTITY = DogBreedEntity("Pug", "pug")
        val SOME_DOG_BREED_ENTITY_WITH_SUB_BREED =
            DogBreedEntity("Golden Retriever", "retriever", "golden")
        const val SOME_RANDOM_IMAGE_COUNT = 30
    }

    private val mockDogBreedsService: DogBreedsService = mockk {
        coEvery { getAllBreeds() }.returns(SOME_DOG_BREED_LIST_RESPONSE)
        coEvery { getRandomBreedImages(any(), any()) }.returns(SOME_DOG_BREED_IMAGES_RESPONSE)
    }

    private val mockDogBreedListResponseMapper: DogBreedListResponseMapper = mockk {
        every { map(any()) }.returns(emptyList())
    }

    private val mockDogBreedImagesResponseMapper: DogBreedImagesResponseMapper = mockk {
        every { map(any()) }.returns(emptyList())
    }

    private val subject: DogBreedRepository = DogBreedRepositoryImpl(
        mockDogBreedsService,
        mockDogBreedListResponseMapper,
        mockDogBreedImagesResponseMapper
    )

    @Test
    fun givenDogBreedRepository_whenGetAllBreeds_thenDataSourceIsQueriedAndResultIsMapped() =
        runBlocking {
            subject.getDogBreeds()
            coVerify { mockDogBreedsService.getAllBreeds() }
            verify { mockDogBreedListResponseMapper.map(SOME_DOG_BREED_LIST_RESPONSE) }
        }

    @Test
    fun givenDogBreedRepository_whenGetRandomBreedImages_thenDataSourceIsQueriedAndResultIsMapped() =
        runBlocking {
            subject.getDogBreedImages(SOME_DOG_BREED_ENTITY, SOME_RANDOM_IMAGE_COUNT)
            coVerify { mockDogBreedsService.getRandomBreedImages("pug", SOME_RANDOM_IMAGE_COUNT) }
            verify { mockDogBreedImagesResponseMapper.map(SOME_DOG_BREED_IMAGES_RESPONSE) }
        }

    @Test
    fun givenDogBreedRepository_whenGetRandomBreedImagesWithSubBreed_thenDataSourceIsQueriedAndResultIsMapped() =
        runBlocking {
            subject.getDogBreedImages(SOME_DOG_BREED_ENTITY_WITH_SUB_BREED, SOME_RANDOM_IMAGE_COUNT)
            coVerify {
                mockDogBreedsService.getRandomBreedImages(
                    "retriever/golden",
                    SOME_RANDOM_IMAGE_COUNT
                )
            }
            verify { mockDogBreedImagesResponseMapper.map(SOME_DOG_BREED_IMAGES_RESPONSE) }
        }

    @Test
    fun givenDogBreedRepositoryAndServiceReturnsError_whenGetAllBreeds_thenExceptionIsThrown() =
        runBlocking {
            coEvery {
                mockDogBreedsService.getAllBreeds()
            } returns SOME_DOG_BREED_LIST_RESPONSE.copy(status = "error")

            var servicesException: Exception? = null
            try {
                subject.getDogBreeds()
            } catch (exception: Exception) {
                servicesException = exception
            }
            Truth.assertThat(servicesException).isInstanceOf(
                DogBreedsServiceException::class.java
            )
        }

    @Test
    fun givenDogBreedRepositoryAndServiceReturnsError_whenGetRandomBreedImages_thenExceptionIsThrown() =
        runBlocking {
            coEvery {
                mockDogBreedsService.getRandomBreedImages(
                    any(),
                    any()
                )
            } returns SOME_DOG_BREED_IMAGES_RESPONSE.copy(status = "error")
            var servicesException: Exception? = null
            try {
                subject.getDogBreedImages(
                    SOME_DOG_BREED_ENTITY,
                    SOME_RANDOM_IMAGE_COUNT
                )
            } catch (exception: Exception) {
                servicesException = exception
            }
            Truth.assertThat(servicesException).isInstanceOf(
                DogBreedsServiceException::class.java
            )
        }

}