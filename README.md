# Banking App Challenge

Aplicación Android de ejemplo para login, listado de productos bancarios y detalle de cuenta con movimientos mock.

## Implementación

El proyecto está dividido por módulos para separar responsabilidades:

- `app`: entrada de la aplicación, navegación y shell principal.
- `feature:login`, `feature:accounts`, `feature:account-detail`: pantallas y lógica de presentación por feature.
- `domain`: modelos, contratos de repositorio y casos de uso.
- `data`: implementaciones de repositorio, sesión segura y datos mock.
- `core:ui`: tema, componentes compartidos y base MVI.

La UI está construida con Jetpack Compose y cada pantalla vive en su feature correspondiente. La navegación usa Navigation Compose, mientras que Hilt resuelve la inyección de dependencias entre módulos. En presentación, los `ViewModel` exponen estado con `StateFlow` y eventos de una sola vez con `SharedFlow`, lo que permite una comunicación clara entre lógica y UI.

En la capa de negocio, `domain` concentra los casos de uso y contratos de repositorio para evitar acoplar la UI con los detalles de datos. La capa `data` implementa esos contratos con repositorios mock y manejo de sesión, lo que hace posible probar el flujo completo de la app sin depender de un backend real.

## Flujo general

El usuario inicia sesión, entra al home con la lista de productos, puede consultar sus cuentas y luego navegar al detalle de una cuenta para ver sus movimientos. Todo ese recorrido reutiliza la misma estructura: pantalla -> `ViewModel` -> caso de uso -> repositorio.

## Patrones usados y por qué

- Modularización: facilita escalar el proyecto y mantener cada feature aislada.
- MVI: hace más predecible el estado de pantalla y simplifica el manejo de eventos y efectos.
- Use cases + repositories: separan la lógica de negocio de la implementación de datos, lo que mejora testabilidad y mantenimiento.
- Hilt: reduce boilerplate y centraliza la resolución de dependencias.

Se eligieron mocks en `data` para enfocarse en la experiencia y la arquitectura sin depender de un backend real.
