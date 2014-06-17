import java.util.Date

/*
An idea starts life as an unsaved idea, with just a stock and an open date.
When it gets saved, it becomes a saved idea, which also has an ID. When it gets
closed, it becomes a closed idea, which also has a close date.

An exciting way to model this is with a higher-kinded type. Idea has two
covariant higher-kinded type parameters, both bounded to be subtypes of Option,
which are then used to define the ID and close date fields. Uses of the Idea
type in the program bind the parameters to appropriate subtypes of Option (Some
if the value should be present, None if it shouldn't, or Option if they don't
care).

This offers compile-time protection against trying to perform operations on
ideas which are in the wrong stage of their lifecycle. It requires no code
duplication.

Two weaknesses with this approach are that it requires redundant Option
boilerplate (eg having to pass Some(x) instead of x to a constructor) and that
it involves a scary wizard-level Scala feature, the higher-kinded type.
*/

case class Idea[+IfSaved[_] <: Option[_], +IfClosed[_] <: Option[_]](id: IfSaved[String], stock: String, openDate: Date, closeDate: IfClosed[Date])

object FunctionsThatTakeIdeas {
	// something that does not require any additional fields
	def format(idea: Idea[Option, Option]): String = {
		idea.stock + "@" + idea.openDate
	}
	
	// something that requires an ID
	def query(idea: Idea[Some, Option]): String = {
		idea.id.get
	}
	
	// something that requires a close date
	def duration(idea: Idea[Option, Some]): Long = {
		idea.closeDate.get.getTime - idea.openDate.getTime
	}
}

object Main {
	def main(args: Array[String]): Unit = {
		val unsaved = Idea(None, "VOD.L", new Date(), None)
		println("unsaved: " + unsaved)
		FunctionsThatTakeIdeas.format(unsaved)
		// FunctionsThatTakeIdeas.query(unsaved) // forbidden
		// FunctionsThatTakeIdeas.duration(unsaved) // forbidden
		
		val saved = unsaved.copy(id = Some("beefc4c3"))
		println("saved: " + saved)
		FunctionsThatTakeIdeas.format(saved)
		FunctionsThatTakeIdeas.query(saved)
		// FunctionsThatTakeIdeas.duration(saved) // forbidden
		
		val closed = saved.copy(closeDate = Some(new Date()))
		println("closed: " + closed)
		FunctionsThatTakeIdeas.format(closed)
		FunctionsThatTakeIdeas.query(closed)
		FunctionsThatTakeIdeas.duration(closed)
	}
}

