package com.example.supabase

import androidx.lifecycle.SavedStateHandle
import com.example.supabase.domain.model.User
import com.example.supabase.domain.usecase.CreateUserUseCase
import com.example.supabase.domain.usecase.GetShowUserUseCase
import com.example.supabase.domain.usecase.UpdateUserUseCase
import com.example.supabase.presentation.feature.updateuser.UpdateUserViewModel
import io.mockk.MockKAnnotations
import io.mockk.coVerify
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.impl.annotations.SpyK
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import java.util.UUID

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest() {

    @RelaxedMockK
    lateinit var createUserUseCase: CreateUserUseCase

    @RelaxedMockK
    lateinit var getShowUserUseCase: GetShowUserUseCase

    @RelaxedMockK
    lateinit var updateUserUseCase: UpdateUserUseCase

    @RelaxedMockK
    lateinit var savedStateHandle: SavedStateHandle


    @Before
    fun setUp() = MockKAnnotations.init(this, relaxUnitFun = true)

    @Test
    fun addition_isCorrect() {
        savedStateHandle.set("user_uuid", "1")
        val updateUserViewModel = UpdateUserViewModel(getShowUserUseCase, createUserUseCase, updateUserUseCase, savedStateHandle)
        val user = User(UUID.randomUUID().toString(), "John", "Paul", "JohnPaul@Gmail.com", "10")
        coVerify(exactly = 0) {
            createUserUseCase.execute(any())
        }

        runBlocking {
            updateUserViewModel.createUser(user)
        }

        coVerify(exactly = 1) {
            createUserUseCase.execute(any())
        }
        assertEquals(4, 2 + 2)
    }
}