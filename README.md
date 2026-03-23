# Banking App Challenge

## 1. Qué es esta aplicación

`Banking App Challenge` es una aplicación Android de ejemplo construida para demostrar un flujo bancario básico de punta a punta:

1. login
2. home con navegación inferior
3. listado de cuentas
4. detalle de una cuenta
5. visualización de movimientos

No depende de un backend real. La capa de datos trabaja con repositorios mock, latencias simuladas y escenarios de error controlados. Eso permite validar arquitectura, navegación, manejo de estado y testing sin infraestructura externa.

## 2. Stack tecnológico

- Kotlin
- Jetpack Compose
- Navigation Compose con destinos serializables
- Hilt para inyección de dependencias
- Coroutines + `StateFlow` + `SharedFlow`
- Arquitectura modular
- MVI ligero propio
- `EncryptedSharedPreferences` para persistencia segura de sesión
- JUnit + Turbine + `kotlinx-coroutines-test`

## 3. Estructura modular

El proyecto está dividido en módulos con responsabilidades claras:

### `:app`

Es el punto de entrada de la aplicación.

Responsabilidades:
- inicializar Hilt con `BankingAppApplication`
- levantar la `MainActivity`
- aplicar el tema global
- crear el `NavController`
- definir el `NavHost` y conectar los feature modules
- contener el shell principal del home con bottom navigation

Archivos clave:
- `app/src/main/java/com/stevecampos/bankingapp/BankingAppApplication.kt`
- `app/src/main/java/com/stevecampos/bankingapp/MainActivity.kt`
- `app/src/main/java/com/stevecampos/bankingapp/navigation/AppNavHost.kt`
- `app/src/main/java/com/stevecampos/bankingapp/home/HomeShellScreen.kt`

### `:domain`

Es la capa de negocio pura. No depende de Android.

Responsabilidades:
- definir modelos de dominio
- declarar contratos de repositorio
- encapsular reglas de negocio en casos de uso
- definir excepciones de negocio
- declarar calificadores como `@IoDispatcher`

Esto es importante en entrevista porque demuestra separación de responsabilidades y testabilidad.

Archivos clave:
- `domain/src/main/kotlin/com/stevecampos/domain/usecase/AuthUseCases.kt`
- `domain/src/main/kotlin/com/stevecampos/domain/usecase/AccountUseCases.kt`
- `domain/src/main/kotlin/com/stevecampos/domain/usecase/SessionUseCases.kt`
- `domain/src/main/kotlin/com/stevecampos/domain/model/DomainException.kt`

### `:data`

Implementa los contratos definidos en `domain`.

Responsabilidades:
- autenticación mock
- cuentas y movimientos mock
- almacenamiento seguro del token/sesión
- administración del estado de sesión
- escenarios configurables de éxito/error
- módulos de Hilt para enlazar interfaces con implementaciones

Archivos clave:
- `data/src/main/kotlin/com/stevecampos/data/di/DataModule.kt`
- `data/src/main/kotlin/com/stevecampos/data/di/DispatcherModule.kt`
- `data/src/main/kotlin/com/stevecampos/data/repository/MockAuthRepository.kt`
- `data/src/main/kotlin/com/stevecampos/data/repository/MockAccountsRepository.kt`
- `data/src/main/kotlin/com/stevecampos/data/repository/DefaultSessionRepository.kt`
- `data/src/main/kotlin/com/stevecampos/data/local/EncryptedTokenStore.kt`

### `:core:ui`

Contiene piezas reutilizables de UI y la base arquitectónica común del patrón de presentación.

Responsabilidades:
- tema visual compartido
- componentes reutilizables
- utilidades de UI
- clase base `MviViewModel`

Archivos clave:
- `core/ui/src/main/kotlin/com/stevecampos/core/ui/mvi/MviViewModel.kt`
- `core/ui/src/main/kotlin/com/stevecampos/core/ui/util/CurrencyFormatter.kt`
- `core/ui/src/main/kotlin/com/stevecampos/core/ui/util/EmojiSanitizer.kt`

### `:feature:login`

Módulo de presentación del login.

Responsabilidades:
- pantalla de autenticación
- estado de login
- intents del usuario
- efectos de navegación
- llamada al caso de uso de login

### `:feature:accounts`

Módulo de presentación del listado de cuentas.

Responsabilidades:
- carga inicial y refresh
- saludo al usuario con el nombre de la sesión
- navegación al detalle de cuenta
- manejo de diálogos de error
- controles de debug para simular éxito/error en operaciones mock

### `:feature:account-detail`

Módulo de presentación del detalle de cuenta.

Responsabilidades:
- obtener una cuenta y sus movimientos
- mostrar estado de loading, content o error
- copiar número de cuenta
- compartir datos de cuenta
- redirigir al login ante sesión inválida

## 4. Dependencias entre módulos

La dirección de dependencias está bien planteada:

```text
app -> feature:login
app -> feature:accounts
app -> feature:account-detail
app -> data
app -> domain
app -> core:ui

feature:* -> domain
feature:* -> core:ui

data -> domain

domain -> sin dependencias Android
```

Esto refleja una variante de Clean Architecture:

- `presentation` depende de `domain`
- `data` implementa contratos de `domain`
- `domain` no conoce detalles de UI ni de infraestructura

## 5. Patrón arquitectónico principal

La app combina dos ideas:

### Clean Architecture

Se ve en la separación entre:

- UI/presentación: features + `app`
- negocio: `domain`
- datos: `data`

La UI no llama repositorios concretos. Llama casos de uso, y esos casos de uso dependen de interfaces.

### MVI ligero

Cada feature de presentación sigue una estructura consistente:

- `Intent`: acciones del usuario
- `State`: estado observable de la pantalla
- `Effect`: eventos de una sola vez, como navegación o side effects
- `ViewModel`: recibe intents y transforma estado/efectos

El centro técnico de esto es `MviViewModel`, que expone:

- `StateFlow<S>` para el estado persistente
- `SharedFlow<E>` para efectos efímeros
- helpers como `updateState()` y `executeUseCase()`

En la práctica, el flujo es:

`UI -> Intent -> ViewModel -> UseCase -> Repository -> Result -> ViewModel -> State/Effect -> UI`

## 6. Cómo funciona la inyección de dependencias

La app usa Hilt.

### Punto de entrada

`BankingAppApplication` está anotada con `@HiltAndroidApp`. Eso crea el grafo raíz de dependencias.

### Activity

`MainActivity` usa `@AndroidEntryPoint` para participar en el grafo.

### ViewModels

Cada `ViewModel` de feature usa:

- `@HiltViewModel`
- constructor injection con `@Inject`

Ejemplos:
- `LoginViewModel`
- `AccountsViewModel`
- `AccountDetailViewModel`

### Módulos de Hilt

En `data` están los bindings más importantes:

- `AuthRepository -> MockAuthRepository`
- `AccountsRepository -> MockAccountsRepository`
- `SessionRepository -> DefaultSessionRepository`
- `DebugScenarioRepository -> DefaultDebugScenarioRepository`
- `TokenStore -> EncryptedTokenStore`

Además, `DispatcherModule` provee el `CoroutineDispatcher` etiquetado con `@IoDispatcher`.

Esto está bien hecho porque evita hardcodear `Dispatchers.IO` por todos lados y mejora testabilidad.

## 7. Navegación de la app

La navegación está centralizada en `AppNavHost`.

Destinos:
- `LoginDestination`
- `HomeDestination`
- `AccountDetailDestination(accountId: String)`

Se usan destinos serializables con `kotlinx.serialization`, lo que hace la navegación más segura y expresiva.

### Flujo de navegación real

1. La app arranca en `LoginDestination`
2. Si el login es exitoso, navega a `HomeDestination`
3. El login se saca del back stack con `popUpTo(... inclusive = true)`
4. Dentro del home, la sección "Productos" monta `AccountsRoute`
5. Al tocar una cuenta, navega a `AccountDetailDestination(accountId)`
6. Si ocurre `Unauthorized`, se limpia sesión y se redirige a login

### Home shell

El home no es una pantalla de negocio grande. Es un contenedor.

Tiene dos tabs:
- `PRODUCTOS`: renderiza cuentas
- `OPERACIONES`: hoy muestra un placeholder

Eso también deja preparada la aplicación para crecer con nuevas secciones sin romper la arquitectura existente.
