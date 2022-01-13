### `ApiResult` class
It's convenient to wrap the results of your network calls in some object and then
handle the result of the operation somewhere in your UI or ViewModel layer. 
When creating your repo, use the `ApiResult` class to wrap some API call

```kotlin
class MyRepo(private val api: MyRetrofitApiInterface) {

    suspend fun getSelf(): ApiResult<UserResponse> = ApiResult.wrap { api.getSelf() }

    fun logIn(email: String, password: String): Flow<ApiResult<LogInResponse>> =
        ApiResult.flow { api.logIn(email, password) }
}
```
You can see that you get two functions to use:   
`wrap(call: (suspend () -> T)): ApiResult<T>`  
and  
`flow(call: suspend () -> T): Flow<ApiResult<T>>`  
The "Flow" function first emits a `Loading` response and then executes the suspending call. ApiResult provides robust
interface similar to kotlin.Result class, but with another added state and integration with coroutines. Inspired by
EitherNet ApiResult, but serving completely different purpose and doing different things, this class lets you write
beautiful error and state handling code. Also goes along nicely with MVI and MVVM architectures. For more info, just
check out sources.
