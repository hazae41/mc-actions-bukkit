package hazae41.actions

interface Lang {
  fun error(): String
  fun cancel(): String
  fun ended(): String
  fun cancelHint(): String
  fun messagePrompt(): String
  fun noRunCommand(): String
  fun noSign(): String
  fun done(): String
}

fun getLang(locale: String): Lang {
  val first = locale.split("_")[0]

  if (first == "fr")
    return French
  if (first == "ca")
    return Spanish
  if (first == "es")
    return Spanish
  if (first == "de")
    return German
  return English
}

object English : Lang {
  override fun error() = "An error occured"
  override fun cancel() = "cancel"
  override fun ended() = "Ended"
  override fun cancelHint() = "Type \"${cancel()}\" to cancel"
  override fun messagePrompt() = "Write a message. ${cancelHint()}"
  override fun noRunCommand() = "Running commands is forbidden! Please use command suggestion instead"
  override fun noSign() = "The sign no longer exists"
  override fun done() = "Done!"
}

object French : Lang {
  override fun error() = "Une erreur est survenue"
  override fun cancel() = "annuler"
  override fun ended() = "Terminé"
  override fun cancelHint() = "Tape \"${cancel()}\" pour annuler"
  override fun messagePrompt() = "Entre un message. ${cancelHint()}"
  override fun noRunCommand() = "Exécuter des commandes est interdit ! Utilise plutôt la suggestion de commande"
  override fun noSign() = "La pancarte n'existe plus"
  override fun done() = "C'est bon !"
}

object Spanish : Lang {
  override fun error() = "Se ha producido un error"
  override fun cancel() = "cancelar"
  override fun ended() = "Terminado"
  override fun cancelHint() = "Escriba \"${cancel()}\" para cancelar"
  override fun messagePrompt() = "Escriba un mensaje. ${cancelHint()}"
  override fun noRunCommand() = "¡Ejecutar comandos está prohibido! En su lugar, use la sugerencia de comando"
  override fun noSign() = "El signo ya no existe"
  override fun done() = "¡Está bien!"
}

object German : Lang {
  override fun error() = "Ein Fehler ist aufgetreten"
  override fun cancel() = "absagen"
  override fun ended() = "Beendet"
  override fun cancelHint() = "Geben Sie zum Abbrechen \"${cancel()}\" ein."
  override fun messagePrompt() = "Schreiben Sie eine Nachricht. ${cancelHint()}"
  override fun noRunCommand() =
    "Das Ausführen von Befehlen ist verboten! Bitte benutze stattdessen den Befehlsvorschlag"

  override fun noSign() = "Das Zeichen existiert nicht mehr"
  override fun done() = "Erledigt!"
}
