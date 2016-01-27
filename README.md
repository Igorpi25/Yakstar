Session (Not ready yet)
=======================

Библиотека которая берет на себя ответственность за регистрацию аккаунта, логина и всех операций связанных с авторизацией. Самостоятельно создает и показывает нужные фрагменты. Реализует удобный интерфейс обратного вызова. 

Пример использования
--------------------

```java
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.ivanov.tech.connection.Connection;
import com.ivanov.tech.session.Session;
```

```java
...
    	//Init before call checkApiKey. It just init App Preferences, where are stored api-key and user_id
      Session.Initialize(getApplicationContext());
...        
    	Session.checkApiKey(getActivity(), getSupportFragmentManager(), R.id.main_container, new Connection.ProtocolListener() {
			
			@Override
			public void onCanceled() {
				//Приложение не запустится, пока пользователь не будет авторизован
				finish();
			}
			
			@Override
			public void isCompleted() {
				FragmentManager fragmentManager = getSupportFragmentManager();
		        fragmentManager.beginTransaction()
		                .replace(R.id.main_container, new FragmentDemo())
		                .commit();		
			}
		});
...	
```
Аргументы:
* `getActivity` - контекст активити
* `getFragmentManager` - `supportFragmentManager` из actionbarsherlock (Внимание! Не путайте с нативным getFragmentManager)
* `R.id.main_container` - передается layout используемый в качестве окна активити, тогда диалог будет показан на весь экран. Если передать другой layout, то в качестве окна диалога будет использован переданный вами layout
* ```java new ProtocolListener``` - реализация интерфэйса `ProtocolListener` для обратного вызова. Тут передается операция (другими словами ваш код)

При вызове `checkApiKey`, программа проверяет: существует ли api-key, если существует отправляет его на сервер. Если сервер решит, что api-key актуальный, то программа вызывает метод `isCompleted` переданного `ProtocolListener` объекта; 
Иначе создается и открывается фрагмент `FragmentRegister`, чтобы создать новый аккаунт и получить api-key:

<img src="screenshot_FragmentRegister.png" width="200">

Если у вас уже существует аккаунт, то можете просто выполнить логин. Для этого кликните кнопку "I've an account already", тогда откроется фрагмент `FragmentLogin`:

<img src="screenshot_FragmentLogin.png" width="200">

Как только используя фрагменты `FragmentLogin` и `FragmentRegister` в конце-концов получите apikey, будет вызван метод `isCompleted`.

*ВАЖНО!* Нужно обязательно сделать инициализировацию, вызывав `Session.Initialize(getApplicationContext())` в `onCreate` методе `Activity`. В качестве параметра передается `ApplicationContext`

Вызов `checkApiKey` как правило нужно делать при старте приложения. Автор рекомендует делать это внутри `onCreate` первого запустившегося активити. Таким образом пользователь войдет в приложение, только если выполнит авторизацию (обратите внимание на `finish()` внутри `onCanceled` переданного `ProtocolListener` объекта)

Что такое apikey
----------------

После вызова `checkApiKey` программа отправляет на сервер пароль и логин, в ответ получает sha1 ключ (`apikey`). Полученный ключ используется вместо логина и пароля. 
При повторном отправлении логина и пароля(даже если будет отправлен из другого устройства), имеющийся sha1 ключ становится неактуальным.
На одного пользователя выдается один sha1 ключ.

Серверная часть
---------------
Серверную часть вы можете видеть в репозитории GitHub [Server][4]. Есть инструкция по самостоятельному запуску и настройке сервера

Используемые библиотеки
-----------------------

* [ActionBarSherlock][1]
* [Volley][2]
* [Connection][3]- библиотека автора. Используется как подмодуль



[1]: http://actionbarsherlock.com/
[2]: https://github.com/mcxiaoke/android-volley
[3]: https://github.com/Igorpi25/Connection
[4]: https://github.com/Igorpi25/Server
