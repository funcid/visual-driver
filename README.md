# Cristalix Animation API DOCS (актуальная версия 1.1.38)

<br>
<h2>Зачем нужно использовать этот инструмент?</h2>

1. Чтобы добавлять новые глобальные моды взаимодействующих с сервером (например новая персонализация - граффити)
2. Для упращения работы с модами, вынесенные утилиты вам помогут не писать лишний код
3. Автоматическое обновление базового пакета модов (стандартный пакет, квесты, лутбокс и прочее)
4. Единый стиль и оформление режимов
<br>

<h2>Как подключить? Какие модули есть?</h2>

<h3>Get Started `with .gradle`</h3>

```
repositories {
  mavenLocal()
  mavenCentral()
  maven {
    url 'https://repo.implario.dev/cristalix/'
    credentials {
      username System.getenv("IMPLARIO_REPO_USER")
      password System.getenv("IMPLARIO_REPO_PASSWORD")
    }
  }
}

dependencies {
  implementation 'me.func:animation-api:1.1.22'
  implementation 'org.jetbrains.kotlin:kotlin-stdlib:1.6.0'
}
```

<h3>Модули</h3>

`STANDARD` - стандартный набор модов (by <a href="https://vk.com/funcid">@func</a>, <a href="https://vk.com/delfikpro">@delfikpro</a>, <a href="https://vk.com/sworroo">@sworroo</a>)<br>
`DIALOG` - кит с квестовыми диалогами (by <a href="https://vk.com/sworroo">@sworroo</a>, <a href="https://vk.com/funcid">@func</a>)<br>
`LOOTBOX` - набор для подключения лутбоксов (by <a href="https://vk.com/delfikpro">@delfikpro</a>, <a href="https://vk.com/funcid">@func</a>)<br>
`MULTICHAT` - набор для мультичата (by <a href="https://vk.com/zabelovq">@zabelov</a>)<br>
`GRAFFITI` - кит с клиентом к сервису граффити и подключением мода (by <a href="https://vk.com/funcid">@funcid</a>)<br>
`EXPERIMENTAL` - экспериментальный набор для тестирования новых модов (by <a href="https://vk.com/funcid">@funcid</a>)<br>
`NPC` - модуль для работы с NPC (by <a href="https://vk.com/funcid">@funcid</a>)<br>

<h3>Подключение модулей</h3>

При старте плагина напишите `Anime.include(Kit.STANDARD)`, так вы подключите стандартный набор модов, если вам нужны другие модули, например Kit.LOOTBOX, Kit.DIALOG, Kit.STANDARD (другие будут добавлены позже), то укажите их через запятую: `Anime.include(Kit.LOOTBOX, Kit.DIALOG, Kit.STANDARD)`

<h3>Индикаторы (модуль STANDARD)</h3>

<img src="https://user-images.githubusercontent.com/42806772/144913370-bb9ecbad-a5be-40c0-95b2-158b0bcdc0ad.png" width="500">

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

<h3>Мультичат</h3>

<img src="https://user-images.githubusercontent.com/42806772/146237931-759b0902-50e4-44e3-a692-409089ef7d6a.png" width="500">

Чаты по умолчанию: <br>

```
  SERVER
  SYSTEM
  ATTACK
  PARTY
  TRADE
```

Методы взаимодействия с клинтом:<br>
`Anime.chat(player: Player, chat: ModChat, message: String)` отправить игроку сообщение в чат <br>
`Anime.removeChats(player: Player, vararg chats: ModChat)` удалить все указанные в чаты у игрока <br>

<h3>Подсвечивание границ экрана, виньетка (модуль STANDARD)</h3>

<img src="https://user-images.githubusercontent.com/42806772/144913800-298a8e7b-22bf-4000-a03f-f32891459b63.png" width="500">

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

<h3>Подсвечивание границ места в мире (модуль EXPERIMENTAL)</h3>

<img src="https://user-images.githubusercontent.com/42806772/146236013-43fbd0d1-4aa3-4a8c-b0a2-2ed37260575a.png" width="500">

Методы добавления места в глобальный список мест (не показывает игрокам!):<br>
`Glow.addPlace(red: Int, blue: Int, green: Int, x: Double, y: Double, z: Double, onJoin: (Player) -> Unit)`<br>
`Glow.addPlace(red: Int, blue: Int, green: Int, x: Double, y: Double, z: Double)`<br>
`Glow.addPlace(color: GlowColor, x: Double, y: Double, z: Double, onJoin: (Player) -> Unit)`<br>
`Glow.addPlace(color: GlowColor, x: Double, y: Double, z: Double)`<br>
`Glow.addPlace(place: GlowingPlace, onJoin: Consumer<Player>)`<br>
`Glow.addPlace(place: GlowingPlace)`<br>

Методы показа мест игроку:<br>
`Glow.showPlace(player: Player, place: GlowingPlace)` показать игроку место (место может быть не глобальным!)<br>
`Glow.showLoadedPlace(player: Player, uuid: UUID)` показать игрку глобальное место по UUID<br>
`Glow.showAllPlaces(player: Player)` показать игроку все глобальные места<br>

Очистка мест:<br>
`Glow.removePlace(place: GlowingPlace, vararg players: Player)` удаление места для указанных игроков и удаление из списка глобальных<br>
`Glow.clearPlaces(vararg players: Player)` очистить указанным игрокам места и удалить все места их глобального списка<br>

<h3>NPC (модуль NPC)</h3>

<img src="https://user-images.githubusercontent.com/42806772/147400304-4ffc0399-8fa2-4ef1-8346-ac0ad7e18ab8.gif" width="500">

Методы:<br>
`Npc.npc(init: NpcData.() -> Unit): NpcSmart` конструктор для создания NPC (только через него можно сделать глобальных NPC)<br>
`Npc.spawn(entityId: Int)` показать всем игрокам заранее созданный NPC по entityId (оно есть в NpcData)<br>
`Npc.kill(entityId: Int)` спрятать всем игрокам заранее созданный NPC по entityId (оно есть в NpcData)<br>
`Npc.hide(entityId: Int, player: Player)` скрыть конкретному игроку NPC по entityId<br>
`Npc.show(entityId: Int, player: Player)` показать конкретному игроку NPC по entityId<br>
`Npc.clear()` удалить всем игрокам всех NPC (не чистит глобальный список NPC)<br>
<br>NpcSmart:<br>

```
    var data: NpcData, // данные на клиенте
    var click: Consumer<PlayerUseUnknownEntityEvent>? = null, // обработчик нажатия на NPC
    private var leftArm: ItemStack? = null, // предмет в левой руке
    private var rightArm: ItemStack? = null, // предмет в правой руке
    private var head: ItemStack? = null, // предмет на голове
    private var chest: ItemStack? = null, // предмет на груди
    private var legs: ItemStack? = null, // предмет на ножках
    private var feet: ItemStack? = null, // предмет на стопах
```

`NpcSmart.slot(slot: EquipmentSlot, itemStack: ItemStack): NpcSmart` метод обновляющий слот NPC для всех игроков<br>
`NpcSmart.slot(slot: EquipmentSlot, itemStack: ItemStack, player: Player): NpcSmart` метод обновляющий слот NPC для конкретного игрока<br>
`NpcSmart.kill(): NpcSmart` скрыть всем игрокам этого NPC<br>
`NpcSmart.show(player: Player): NpcSmart` показать игроку этого NPC<br>
`NpcSmart.hide(player: Player): NpcSmart` скрыть игроку этого NPC<br>
`NpcSmart.update(player: Player): NpcSmart` обновить игроку данные NPC (имя, координаты, поворот головы, положение тела - сидеть, спать, шифтить)<br> 
`NpcSmart.swingArm(mainHand: Boolean, player: Player): NpcSmart` пошевелить рукой, указав главная ли рука и игрока-получателя<br> 
`NpcSmart.spawn(): NpcSmart` отправить всем игрокам NPC<br>
<br>NpcData:<br>

```
    var id: Int = (Math.random() * Int.MAX_VALUE).toInt(), // entityId NPC
    val uuid: UUID = UUID.randomUUID(), // UUID
    var x: Double = 0.0, 
    var y: Double = 0.0,
    var z: Double = 0.0,
    var type: Int = 1000, // тип сущности 1000 - ИГРОК
    var name: String? = null, // имя
    var behaviour: NpcBehaviour = NpcBehaviour.NONE, // модель поведения NPC
    var pitch: Float = 0f,
    var yaw: Float = 0f,
    var skinUrl: String? = null, // ссылка на скин 
    var skinDigest: String? = null, 
    var slimArms: Boolean = false, // тонкие руки
    var sneaking: Boolean = false, // шифтит
    var sleeping: Boolean = false, // спит
    val sitting: Boolean = false // сидит
```

`NpcData.onClick(init: Consumer<PlayerUseUnknownEntityEvent>)` при клике на NPC обработать событие<br>
`NpcData.location(location: Location)` указать координаты NPC через локацию<br>
<br>Модели поведения:<br>

```
    NONE, // нет поведения
    STARE_AT_PLAYER, // следит за игроком
    STARE_AND_LOOK_AROUND // смотрит на игрока
```

<br>Пример на Kotlin:<br>

```
        npc {
            x = 1337.0
            y = 225.5
            z = -102.3
            behaviour = NpcBehaviour.STARE_AND_LOOK_AROUND
            onClick {
                it.player.sendMessage("Хай")
            }
        }.slot(EquipmentSlot.HAND, CraftItemStack.asNMSCopy(ItemStack(Material.DIAMOND_BLOCK))).spawn()
```

<h3>Диалоги (модуль DIALOGS)</h3>

<img src="https://user-images.githubusercontent.com/42806772/144916818-ff974368-878c-483d-a34b-79e3f2e576c8.png" width="500">

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
      Action("/hub"),
      new Action(Actions.CLOSE)
    )
  )
))
```

<h3>Меню ежедневных наград (модуль STANDARD)</h3>

<img src="https://user-images.githubusercontent.com/42806772/144913475-3ea8a658-2630-45d4-94ad-b637dc2b8665.png" width="500">

Метод показа окна награды:<br>
`Anime.openDailyRewardMenu(player: Player, currentDayIndex: Int, vararg week: DailyReward)` показать меню игроку с указанием текущего дня [0..6], а также с указанием наград за каждый день в объекте `DailyReward(имя предмета, сам предмет)`, дневных наград должно быть всегда указано 7 штук

<h3>Лутбокс (модуль LOOTBOX)</h3>

<img src="https://user-images.githubusercontent.com/42806772/144919787-00cdac9a-a01c-4c48-97af-62c2f8e5f8b5.png" width="500">

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

<img src="https://user-images.githubusercontent.com/42806772/144920685-cf487bdc-ff08-4c19-9429-d54050d1bc32.png" width="500">

Что такое маркер? Это случайный UUID генерируемый в конструкторе, координаты в текущем мире игрока x, y, z, размер текстуры и путь к текстуре на клиенте (обычно через resource-pack, но можно загрузить игроку текстуру через метод в разделе "Прочее"). Для упращения работы с текстурами, вынесено несколько базовых в `enum MarkerSign`: 
```
    FINE("textures/others/znak_v_3.png"),
    ERROR("textures/others/znak_v_2.png"),
    WARNING("textures/others/znak_v_1.png"),
    QUESTION_FINE("textures/others/z1.png"),
    QUESTION_ERROR("textures/others/z2.png"),
    QUESTION_WARNING("textures/others/z1.png"),
    ARROW_DOWN("mcpatcher/cit/others/badges/arrow_down.png"),
    ARROW_UP("mcpatcher/cit/others/badges/arrow_up.png"),
    ARROW_RIGHT("mcpatcher/cit/others/badges/arrow_right.png"),
    ARROW_LEFT("mcpatcher/cit/others/badges/arrow_left.png"),
```
Пример маркера: `new Marker(225, 1, 5, 10, MarkerSign.ERROR)`

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

<img src="https://user-images.githubusercontent.com/42806772/144923669-6c52ffef-9359-4175-be7c-d6ead8d4ef3f.png" width="500">

`Anime.topMessage(player: Player, message: String)` сообщение сверху (поддерживает цвета и многострочные сообщения)<br>

<img src="https://user-images.githubusercontent.com/42806772/144923731-bb1d8a58-eb6d-4bd8-97e1-038cf3b5cda4.png" width="500">

`Anime.title(player: Player, text: String)` многострочный title, поддерживает все цвета, а так же `\n`<br>
`Anime.title(player: Player, vararg text: String)` то же самое, но строчк можно указывать через запятую или можно передавать массив<br>

<img src="https://user-images.githubusercontent.com/42806772/144923540-7f2822be-efc1-4f1e-9e34-b4f3e1e704b4.png" width="500">

`Anime.alert(player: Player, title: String, description: String, seconds: Double)` отправить сообщение с комментарием с указанием времени<br>
`Anime.alert(player: Player, title: String, description: String)` отправить сообщение с комментарием (на 7.3 секунды)<br>
`Anime.alert(player: Player, description: String, seconds: Double)` отправить отписание с сообщением "Внимание" с указанием вреени<br>
`Anime.alert(player: Player, description: String)` комментарий с сообщением "Внимание" на 7.3 секунды<br>

<img src="https://user-images.githubusercontent.com/42806772/144923936-a99a7103-7ac3-4dcd-b78a-462358382c20.png" width="500">

`Anime.cursorMessage(player: Player, message: String)` сообщения появляющиеся на курсоре и летящие вниз<br>

<img src="https://user-images.githubusercontent.com/42806772/144924621-7f1d87ae-f0df-4943-af8f-18324cb99124.gif" width="500">

`Anime.itemTitle(player: Player, item: ItemStack, title: String?, subtitle: String?, duration: Double)` отправить на экран предмет с указанием продолжительности в секундах (можно указать сообщения сверху и снизу)<br>

<img src="https://user-images.githubusercontent.com/42806772/144925126-c524e4e4-2cc5-45d1-8647-e5cf98301152.gif" width="500">

`Anime.counting321(player: Player)` начать на экране игрока отсчет 3 2 1 GO!<br>

<h3>Системная информация (модуль STANDARD)</h3>

<img src="https://user-images.githubusercontent.com/42806772/144923255-bb93a4eb-0ba8-4e1e-bf78-0c96332afcac.png" width="500">

`Anime.killboardMessage(player: Player, text: String)` отправить в правую верхнюю часть экрана сообщение<br>
`Anime.killboardMessage(player: Player, text: String, topMargin: Int)` отправить в правую верхнюю часть экрана сообщение с указанием отсупа сверху<br>

<img src="https://user-images.githubusercontent.com/42806772/144923338-130365f6-bea4-4d4d-a9d1-8ec8d90dd7ad.png" width="500">

`Anime.timer(player: Player, text: String, duration: Int, red: Int, blue: Int, green: Int)` начать игроку отчсет сверху с сообщением, длительностью, цветом полоски<br>
`Anime.timer(player: Player, text: String, duration: Int)` начать отсчет сверху с сообщением и продолжительностью<br>
`Anime.timer(player: Player, duration: Int)` начать отсчет с сообщением по умолчанию<br>

<h3>Прочее (модуль STANDARD)</h3>

<img src="https://user-images.githubusercontent.com/42806772/144923420-56720196-99c3-4bc1-8fa1-597971e05a3c.png" width="500">

`Anime.corpse(to: Player, name: String?, uuid: UUID, x: Double, y: Double, z: Double)` создать труп по UUID трупа игрока с Cristalix и координат локации (имя трупу указывать необязательно)<br>
`Anime.corpse(to: Player, name: String?, skinUrl: String, x: Double, y: Double, z: Double)` то же самое, но можно ставить скин трупу по любой ссылке (имя трупу указывать необязательно)<br>
`Anime.clearAllCorpses(player: Player)` очистить игроку все трупы
<br><br>
<b>Отправить пустой Buffer игроку в канал</b>:<br>

`Anime.sendEmptyBuffer(channel: String, player: Player)`<br>

<b>Персонализация:</b><br>

`Anime.lockPersonalization(player: Player)` заблокировать персонализацию игроку<br>
`Anime.unlockPersonalization(player: Player)` разблокировать персонализацию игроку<br>

<b>Загрузка фотографий</b>:

`Anime.loadTexture(player: Player, url: String)` загрузить игроку фотографию по ссылке в папку `cache/animation/`<br>
`Anime.loadTextures(player: Player, vararg url: String)` загрузить игроку фотографии по ссылкам в папку `cache/animation/`<br>
Когда игрок скачает фотографию/и от отправит серверу пустой буффер в канал `func:loaded` <br>

<b>Регистрация канала для входящих сообщений</b>:

`createReader(channel: String, listener: PluginMessageListener)`

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
<img src="https://user-images.githubusercontent.com/42806772/144771556-c024a5fc-910e-4c23-bb95-ab0c58d8bc3a.png" width="500">

Пример #2 (Kotlin):<br>
<img src="https://user-images.githubusercontent.com/42806772/144771588-b6836e50-0c0d-4fee-8fe5-54b681d25ecd.png" width="500">

Пример #3:<br>
<img src="https://user-images.githubusercontent.com/42806772/144771670-73be5c2a-173b-4da9-8ee0-d67a8b291a61.png" width="500">
