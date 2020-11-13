package hazae41.actions

import net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND
import net.md_5.bungee.chat.ComponentSerializer
import org.bukkit.NamespacedKey
import org.bukkit.block.Sign
import org.bukkit.conversations.ConversationContext
import org.bukkit.conversations.ConversationFactory
import org.bukkit.conversations.MessagePrompt
import org.bukkit.conversations.Prompt
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority.MONITOR
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.SignChangeEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.persistence.PersistentDataType.STRING
import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin(), Listener {
  val HEADER = "[Action]"

  override fun onEnable() {
    super.onEnable()

    server.pluginManager.registerEvents(this, this)
  }

  fun key(key: String) = NamespacedKey(this, key)

  @EventHandler(priority = MONITOR)
  fun onplacesign(e: SignChangeEvent) {
    val header = e.getLine(0)
    if (header != HEADER) return
    e.setLine(0, "§c$HEADER")

    val lang = getLang(e.player.locale)

    val prompt = SimplePrompt(lang.messagePrompt()) { _, input ->
      try {
        val comps = ComponentSerializer.parse(input)

        for (comp in comps) {
          if (comp.clickEvent?.action != RUN_COMMAND) continue
          return@SimplePrompt NextMessagePrompt(lang.noRunCommand(), this)
        }

        val sign = e.block.state as Sign
        val data = sign.persistentDataContainer

        data.set(key("action"), STRING, "message")
        data.set(key("message"), STRING, input)

        sign.setLine(0, "§e$HEADER")

        if (sign.update())
          EndMessagePrompt(lang.done())
        else
          EndMessagePrompt(lang.noSign())
      } catch (e: Exception) {
        NextMessagePrompt(lang.error(), this)
      }
    }

    val prefix = "§e[Actions] §r"

    val factory = ConversationFactory(this)
      .withPrefix { prefix }
      .withFirstPrompt(prompt)
      .withTimeout(60)
      .withLocalEcho(false)
      .withModality(true)
      .withEscapeSequence(lang.cancel())
      .addConversationAbandonedListener {
        e.player.sendMessage(prefix + lang.ended())
      }

    factory.buildConversation(e.player)
      .begin()
  }

  @EventHandler(priority = MONITOR)
  fun onclicksign(e: PlayerInteractEvent) {
    if (e.action != Action.RIGHT_CLICK_BLOCK) return
    val sign = e.clickedBlock?.state as? Sign ?: return
    if (sign.getLine(0) != "§e$HEADER") return
    val data = sign.persistentDataContainer

    val action = data.get(key("action"), STRING)

    if (action == "message") {
      val message = data.get(key("message"), STRING)!!
      val comps = ComponentSerializer.parse(message)
      e.player.spigot().sendMessage(*comps)
    }
  }
}

fun SimplePrompt(prompt: String, block: Prompt.(ConversationContext, String) -> Prompt?): Prompt {
  return object : Prompt {
    override fun getPromptText(context: ConversationContext) = prompt
    override fun blocksForInput(context: ConversationContext) = true
    override fun acceptInput(context: ConversationContext, input: String?) = block(context, input!!)
  }
}

class NextMessagePrompt(val message: String, val next: Prompt) : MessagePrompt() {
  override fun getPromptText(context: ConversationContext) = message
  override fun getNextPrompt(context: ConversationContext) = next
}

class EndMessagePrompt(val message: String) : MessagePrompt() {
  override fun getPromptText(context: ConversationContext) = message
  override fun getNextPrompt(context: ConversationContext) = END_OF_CONVERSATION
}