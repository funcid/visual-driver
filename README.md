# Cristalix Animation API DOCS (актуальная версия 1.0.2)

<br>
<h2>Зачем нужно использовать этот инструмент?</h2>

1. Чтобы добавлять новые глобальные моды взаимодействующих с сервером (например новая персонализация - граффити)
2. Для упращения работы с модами, вынесенные утилиты вам помогут не писать лишний код
3. Автоматическое обновление базового пакета модов (стандартный пакет, квесты, лутбокс и прочее)
4. Единый стиль и оформление режимов
<br>
<h2>Каким образом происходит интеграция API?</h2>

1. Вам нужно подключить его внутрь вашего плагина, Animation API - <b>НЕ ПЛАГИН</b><br>
1.1. Добавть `.jar` файл в classpath, последнюю версию вы можете получить у <a href="https://vk.com/funcid">@funcid</a><br>
1.2. Добавить `implementation 'me.func:animation-api:1.0.2'` в ваш `build.gradle`, для получения ключа к репозиторию, пишите <a href="https://vk.com/funcid">@funcid</a> или <a href="https://vk.com/delfikpro">@delfikpro</a>.<br>
1.3. Библиотека работает на языках `Java`, `Kotlin`, `Groovy`<br>
1.4. <b>Совместима со всеми версиями Core API и Paper</b><br>

<h2>Как подключить? Какие модули есть?</h2>

<h3>Подключение модулей</h3>

При старте плагина напишите `Anime.include(Kit.STANDARD)`, так вы подключите стандартный набор модов, если вам нужны другие модули, например Kit.LOOTBOX, Kit.DIALOG, Kit.STANDARD (другие будут добавлены позже), то укажите их через запятую: `Anime.inlude(Kit.LOOTBOX, Kit.DIALOG, Kit.STANDARD)`

<h3>Индикаторы (модуль STANDARD)</h3>

![image](https://user-images.githubusercontent.com/42806772/144913370-bb9ecbad-a5be-40c0-95b2-158b0bcdc0ad.png)

Список доступных индикаторов (`enum Indicators`): <br>
`HEALTH` индикатор здоровья над инвентарем, <br>
`EXP` индикатор уровня, <br>
`HUNGER`индикатор голода игрока, <br>
`ARMOR` индикатор брони, <br>
`VEHICLE` индикатор здоровья управляемого моба (свинья, лощадь...), <br>
`TAB` список игроков при нажатии TAB<br>
`HOTBAR` 9 слотов инвенторя игрока снизу экрана<br>

Методы взаимодействия с клинтом:<br>
`Anime.hideIndicator(player: Player, vararg indicator: Indicators)` скрывает игроку указанные через запятую индикаторы<br>
`Anime.showIndicator(player: Player, vararg indicator: Indicators)` показывает игроку указанные через запятую индикаторы<br>

<h3>Подсвечивание границ экрана, виньетка (модуль STANDARD)</h3>

![2021-12-06_22 59 05](https://user-images.githubusercontent.com/42806772/144913800-298a8e7b-22bf-4000-a03f-f32891459b63.png)

Цвета:
1. Цвета состоят из трез целых чисел и одного дробного, красный [0 .. 255], синий [0 .. 255], зеленый [0 .. 255], прозрачность [0 .. 1.0]
2. Вы можете еспользовать `enum GlowColor` с готовыми частыми цветами

Методы взаимодействия с клинтом:<br>
`Glow.set(player: Player, red: Int, blue: Int, green: Int, alpha: Double)` ставит игроку свечение <br>
`Glow.set(player: Player, color: GlowColor)` то же самое, но через GlowColor<br><br>
`Glow.animate(player: Player, seconds: Double, red: Int, blue: Int, green: Int, alpha: Double)` плавное появление и скрытие свечения с указанием длительности этой анимации<br>
`Glow.animate(player: Player, seconds: Double, color: GlowColor, alpha: Double)` то же самое, но через GlowColor<br>
`Glow.animate(player: Player, seconds: Double, red: Int, blue: Int, green: Int)` то же самое, но без указания прозрачности (без прозрачности)<br>
`Glow.animate(player: Player, seconds: Double, color: GlowColor)` то же самое, но через GlowColor<br>

<h3>Диалоги (модуль DIALOGS)</h3>

![2021-12-06_23 22 49](https://user-images.githubusercontent.com/42806772/144916818-ff974368-878c-483d-a34b-79e3f2e576c8.png)

Методы взаимодействия с клинтом:<br>
`Anime.sendDialog(player: Player, dialog: Dialog)` отправляет игру ветку диалогов<br>
`Anime.openDialog(player: Player, dialogId: String)` показывает игроку диалог по ID ветки<br>
`Anime.dialog(player: Player, dialog: Dialog, openEntrypoint: String)` отправляет и показывает игроку диалог по ID ветки<br>

Структура диалога:
`Dialog` состоит из набора точек входа `Entrypoint`, в одном хранятся `Screen`, это конкоетно что видит игрок - набор строк и кнопок `Button` с действиями `Action`, например открытия нового окна. Набор Action вы можете увидить в `enum Actions`

Пример кода:<br>
```
new Dialog(new Entrypoint(
  "id", 
  "название", 
  new Screen("Первая видимая строка", "Вторая видимая строка").buttons(
    new Button("Имя кнопки").actions(
      new Action(Actions.COMMAND).command("/next"),
      new Action(Actions.CLOSE)
    ), new Button("Войти в очередь").actions(
      new Action(Actions.COMMAND).command("/hub"),
      new Action(Actions.CLOSE)
    )
  )
))
```

<h3>Меню ежедневных наград (модуль STANDARD)</h3>

![image](https://user-images.githubusercontent.com/42806772/144913475-3ea8a658-2630-45d4-94ad-b637dc2b8665.png)

Метод показа окна награды:<br>
`Anime.openDailyRewardMenu(player: Player, currentDayIndex: Int, vararg week: DailyReward)` показать меню игроку с указанием текущего дня [0..6], а также с указанием наград за каждый день в объекте `DailyReward(имя предмета, сам предмет)`, дневных наград должно быть всегда указано 7 штук

<h3>Лутбокс (модуль LOOTBOX)</h3>

![2021-12-06_23 45 04](https://user-images.githubusercontent.com/42806772/144919787-00cdac9a-a01c-4c48-97af-62c2f8e5f8b5.png)

Метод показа дропа с лутбокса:<br>
`Anime.openLootBox(player: Player, vararg items: LootDrop)` открыть игроку лутбокс с указанием всего что выпало в виде объектов `LootDrop(сам предмет, название предмета, <НЕ ОБЯЗАТЕЛЬНО ПО УМАЛЧАНИЮ COMMON> редкость)`

Редкости предметов:
```
enum class DropRare(val title: String, val color: String) {
    COMMON("Обычный", "§a"),
    RARE("Редкий", "§9"),
    EPIC("Эпический", "§5"),
    LEGENDARY("Легендарный", "§6"),;
    
    fun getColored(): String {
        return "$color$title" // метод для получения окрашенного имени редкости
    }
}
```

<h3>Маркеры в мире (модуль STANDARD)</h3>

![image](https://user-images.githubusercontent.com/42806772/144920685-cf487bdc-ff08-4c19-9429-d54050d1bc32.png)

Что такое маркер? Это случайный UUID генерируемый в конструкторе, координаты в текущем мире игрока x, y, z, размер текстуры и путь к текстуре на клиенте (обычно через resource-pack, но можно загрузить игроку текстуру через метод в разделе "Прочее"). Для упращения работы с текстурами, вынесено несколько базовых в `enum MarkerSing`: 
```
    FINE("textures/others/znak_v_3.png"),
    ERROR("textures/others/znak_v_2.png"),
    WARNING("textures/others/znak_v_1.png"),
    QUESTION_FINE("textures/others/z1.png"),
    QUESTION_ERROR("textures/others/z2.png"),
    QUESTION_WARNING("textures/others/z1.png"),
```
Пример маркера: `new Marker(225, 1, 5, 10, MarkerSing.ERROR)`

Методы взаимодействия с клинтом:<br>
`Anime.marker(player: Player, marker: Marker)` показать маркер игроку<br>
`Anime.markers(player: Player, vararg markers: Marker)` показать игроку кучу маркеров<br>
<br>
`Anime.clearMarkers(player: Player)` очистить все маркеры у игрока<br>
`Anime.removeMarker(player: Player, marker: Marker)` удалить игроку маркер по маркеру<br>
`Anime.removeMarker(player: Player, uuid: UUID)` удалить игроку маркер по UUID маркера<br>
<br>
`Anime.moveMarker(player: Player, marker: Marker)` обновить нахождение маркера у игрока<br>
`Anime.moveMarker(player: Player, marker: Marker, seconds: Double)` обновить нахождение маркера у игрока анимированно за указанное время<br>
`Anime.moveMarker(player: Player, uuid: UUID, toX: Double, toY: Double, toZ: Double, seconds: Double)` обновить нахождение маркера у игрока анимированно за указанное время имея только UUID маркера<br>

<h3>Всплывающие сообщения (модуль STANDARD)</h3>


<h3>Системная информация (модуль STANDARD)</h3>

<h3>Прочее (модуль STANDARD)</h3>

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
