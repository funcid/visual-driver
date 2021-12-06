# Cristalix Animation API DOCS (актуальная версия 1.0.2)

<br>
<h2>Зачем нужно использовать этот интсрумент?</h2>

1. Чтобы добавлять новые глобальные моды взаимодействующих с сервером (например новая персонализация - граффити)
2. Для упращения работы с модами, вынесенные утилиты вам помогут не писать лишний код
3. Автоматическое обновление базового пакета модов (стандартный пакет, квесты, лутбокс и прочее)
4. Единый стиль и оформление режимов
<br>
<h2>Каким образом происходит интеграция API?</h2>

1. Вам нужно подключить его внутрь вашего плагина, Animation API - <b>НЕ ПЛАГИН</b><br>
1.1. Добавть `.jar` файл в classpath, последнюю версию вы можете получить у <a href="https://vk.com/funcid">@funcid</a><br>
1.2. Добавить `implementation 'me.func:animation-api:1.0.2'` в ваш `build.gradle`, для получения ключа к репозиторию, пишите <a href="https://vk.com/funcid">@funcid</a> или <a href="https://vk.com/delfikpro">@delfikpro</a>.
<br>

<h2>Как подключить? Какие модули есть?</h2>

При старте плагина напишите `Anime.inlude(Kit.STANDARD)`, так вы подключите стандартный набор модов, если вам нужны другие модули, например Kit.LOOTBOX, Kit.DIALOG, Kit.STANDARD (другие будут добавлены позже), то укажите их через запятую: `Anime.inlude(Kit.LOOTBOX, Kit.DIALOG, Kit.STANDARD)`

<h2>Скачивание, загрузка и отправка ваших модов - утилита ModLoader</h2>

<h3>Скачивание модов</h3>

`download(fileUrl: String, saveDir: String)` скачивание файла по ссылке, в указанную папку (если файл уже скачан - он перезапишется), пример `download("http://cristalix.ru/cristalix-standard-mod.jar", "mods")`
<h3>Загрузка модов в плагин</h3>

`loadFromWeb(fileUrl: String)` загрузка файла в стандартную папку `mods` по адресу, затем прогрузка мода на уровнь плагина<br>
`loadManyFromWeb(vararg fileUrls: String)` то же самое, но можно указывать адреса через запятую<br>
`load(filePath: String)` прогрузка мода указав путь к файлу (не web), мод сохраняется по ключу имени файла, например `mod-bundle.jar`<br>
`loadAll(dirPath: String)` загрузить все моды из папки по путю к папке<br>
<h3>Отправка мода игроку</h3>

`send(modName: String, player: Player)` отправка мода игроку, пример имени `mod-bundle.jar`<br>
`manyToOne(player: Player)` отправить все прогруженные моды игроку<br>
`oneToMany(modName: String)` отправить один мод по имени всем игрокам<br>
`sendAll()` отправить все прогруженные моды всем игрокам<br>

<h2>Отправка данных на моды - конструктор ModTransfer</h2>
Пример #1:<br>
<img src="https://user-images.githubusercontent.com/42806772/144771556-c024a5fc-910e-4c23-bb95-ab0c58d8bc3a.png">
Пример #2 (Kotlin):<br>
<img src="https://user-images.githubusercontent.com/42806772/144771588-b6836e50-0c0d-4fee-8fe5-54b681d25ecd.png">
Пример #3:<br>
<img src="https://user-images.githubusercontent.com/42806772/144771670-73be5c2a-173b-4da9-8ee0-d67a8b291a61.png">
