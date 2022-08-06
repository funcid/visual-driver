package me.func.mod.emotion

import me.func.mod.conversation.ModTransfer
import org.bukkit.entity.Player
import pw.lach.p13n.network.client.P13nChannels.EMOTION_ACTION
import pw.lach.p13n.network.client.PacketEmotionAction
import java.util.UUID

enum class Emotions(val title: String, val uuid: UUID) {
    THING_ABOUT_IT("Think about it", "83ae4880-f3bd-4697-8879-657f494e4d77"),
    BOY("Boy", "d7d8f03e-4cd9-411a-ba1e-2e37bfd0daca"),
    YES("Да", "122faf60-0fb2-44c5-982a-235bb0a7ee85"),
    ROCK_PAPER_SCISSORS("Камень, ножницы, бумага!", "892c6b92-1325-40ea-bd99-c987105e3364"),
    POINTING("Направление", "8659ccca-db8c-46bc-ad8d-5add905fe919"),
    NO("No", "c01f7320-690c-474c-91e3-3afddd44702d"),
    FIST("Тук-тук", "aadaac60-3111-4b6d-bb60-03e36793f285"),
    BITCH_SLAP("Шлепок", "912850c1-44db-44bc-b328-9b93768856e4"),
    T_POSE("Буква T", "324f244d-de6a-4ffc-b7b1-648e9c2bdc08"),
    BOW("Поклон", "24f29364-8efe-4ce6-8407-2614c8b6cf62"),
    CONFUSED("Сконфуженный", "d3b73af7-fdca-411e-88b4-ca30238150f4"),
    SALUTE("Салют", "b4f63219-14ab-4fae-b95b-aac951bdfa0b"),
    BREATHTAKING("breathtaking", "59a8a0c6-c944-403f-936f-24be4a8af1dd"),
    SHRUG("¯\\_(ツ)_/¯", "94f37ae4-de53-4f8f-83bb-e2134df99636"),
    WAVE("Помахать рукой", "795a992c-820b-4431-987e-04b98375dd1f"),
    KICK("Удар", "087b34d7-6fcb-4b98-91fa-1507700860fc"),
    THINKING("Раздумья", "6685836c-7e86-4eda-8374-f58904394338"),
    LAUGHING("Смеюсь", "3c661041-ece5-46d1-bd31-39727b596c18"),
    CLUB_DANCE("Клубный денс", "ebf0e5e6-d869-4741-8ffb-17ea08466b0f"),
    GANGNAM_STYLE("Gangnam Style", "fa7fbdf2-efcc-4a8c-9264-86bbb571b94d"),
    SQUAT_KICK("Squat kick", "a641705b-ad39-415c-beb4-802b9a9906d8"),
    BONGO_CAT("Бонго кот", "69bac7c2-048b-48fc-aca9-531563a41b95"),
    DAB("Дэб", "9a604e94-10d9-4ddb-a12c-00b9bff7c79c"),
    INFINITE_DAB("Бесконечный дэб", "180d39b7-cc90-40cd-9879-db14a4ba7c30"),
    BEST_MATES("Лучшие друзья", "a3aebdae-25e9-4d95-986f-b76c452afc8a"),
    THREATENING("Угроза", "55c8eda9-f011-4272-b9d0-9f79e2071eb1"),
    FACEPALM("Фейспалм", "95c0d797-f643-4959-bbc3-6b94770ad66b"),
    TIDY("Снуп дог", "5e2a9715-34dc-4679-8d25-8b90683c95fc"),
    DISCO("Диско", "a4691314-d43e-4c50-9829-f38c3b5d48cd"),
    TAKE_THE_L("Take the L", "a1df3612-556a-4e1f-b939-09a32f56b8e7"),
    GET_FUNKY("Get funky", "d25e7eae-0061-4703-a2d2-6912d47937d9"),
    PURE_SALT("Соль", "974f9e35-4b7a-4cfb-bf6a-dc08e11ca7d1"),
    EXHAUSTED("Усталость", "6e4d998c-a362-4440-b610-b721a28569c8"),
    CHICKEN("Курица", "2730c81a-c79f-47af-abd8-8976ee7dc2a5"),
    VICTORY_DANCE("Победный танец", "907d0b90-97a2-4f66-b7c7-fd5cb6e2fc27"),
    LIGHT_DANCE("Лёгкий танец", "3f0a646a-c0ef-4005-b61f-375343922395"),
    BONELESS("Без костей", "97cc8a24-4ad9-414c-a89e-9d4fe7b07c05"),
    WOAH("Вах", "1e953467-c105-4603-ae5d-b8114b96933c"),
    STAR_OF_THE_DANCE_FLOOR("Звезда танцпола", "1b4eeba7-73cb-40e3-b16e-86bf8a0239bd"),
    KING_OF_THE_DANCE_FLOOR("Король танцпола", "ff8e4972-c8b5-4682-b349-afbac8f7068d"),
    SHIMMER("Зумба", "ccddba1b-a704-4f19-85ed-e841e6831c8e"),
    CLAPPING("Хлопаю", "5add2e13-defe-4a86-845d-6c097bf3b783"),
    ELECTRO_SHUFFLE("Шаффл", "9c95e7b6-a8f6-4bfb-8511-011b9ab13e53"),
    FLOSS("Флосс", "d7de2ae5-c75e-489c-8aec-d1e6786cedb6"),
    ORANGE_JUSTICE("Orange Justie", "309af21b-d72f-481c-b4da-35563b676b76"),
    DISGUSTED("Отвращение", "4948cda2-9539-4514-b946-68d62e39ca69"),
    POPCORN("Попкорн", "e0727e24-188d-4a18-9933-7ec7c260b2e7"),
    SNEEZE("Чих", "62d3dc36-4a4a-4a0d-a097-c461e42271d5"),
    CRYING("Плачу", "e2546694-33ab-4464-8235-8ee95ac07503"),
    SKIBIDI("Скибиди", "26ae7ce9-da74-4e57-a112-a15b00d36c3e"),
    HYPE("Хайп", "0db21207-ced0-4a2f-bdf9-692f7c590e7d"),
    TWERK("Тверк", "9c416131-282c-4dad-b19d-2d1f69bc789a"),
    ;

    constructor(title: String, uuid: String) : this(title, UUID.fromString(uuid))

    companion object {
        @JvmStatic
        fun play(emotion: UUID, dancer: UUID, vararg receivers: Player) {
            val pk = ModTransfer().json(PacketEmotionAction(dancer, emotion, 0, null))
            receivers.forEach { pk.send(EMOTION_ACTION, it) }
        }
    }

    @JvmName("show")
    fun play(dancer: UUID, vararg receivers: Player): Unit = play(uuid, dancer, *receivers)

    @JvmName("show")
    fun play(dancer: UUID, receivers: Collection<Player>): Unit = play(uuid, dancer, *receivers.toTypedArray())
}

@JvmName("playEmotion")
fun Emotions.play(
    dancer: Player,
    vararg receivers: Player
): Unit = play(dancer.uniqueId, *receivers)

@JvmName("playEmotion")
fun Emotions.play(
    dancer: Player,
    receivers: Collection<Player>
): Unit = play(dancer.uniqueId, *receivers.toTypedArray())
