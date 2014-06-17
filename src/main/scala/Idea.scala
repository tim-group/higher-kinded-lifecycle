import java.util.Date

/*
An idea starts life as an unsaved idea, with just a stock and an open date.
When it gets saved, it becomes a saved idea, which also has an ID. When it gets
closed, it becomes a closed idea, which also has a close date.

The simplest way to model this is with a single class, in which the ID and
close date are optional, and the fields are none or some according to the
stage of its lifecycle the idea is in.

This offers no compile-time protection against trying to perform operations on
ideas which are in the wrong stage of their lifecycle.
*/

case class Idea(id: Option[String], stock: String, openDate: Date, closeDate: Option[Date])

object FunctionsThatTakeIdeas {
	// something that does not require any additional fields
	def format(idea: Idea): String = {
		idea.stock + "@" + idea.openDate
	}
	
	// something that requires an ID
	def query(idea: Idea): String = {
		idea.id.get
	}
	
	// something that requires a close date
	def duration(idea: Idea): Long = {
		idea.closeDate.get.getTime - idea.openDate.getTime
	}
}

object Main {
	def main(args: Array[String]): Unit = {
		val unsaved = Idea(None, "VOD.L", new Date(), None)
		println("unsaved: " + unsaved)
		FunctionsThatTakeIdeas.format(unsaved)
		FunctionsThatTakeIdeas.query(unsaved) // fails
		FunctionsThatTakeIdeas.duration(unsaved) // fails
		
		val saved = unsaved.copy(id = Some("beefc4c3"))
		println("saved: " + saved)
		FunctionsThatTakeIdeas.format(saved)
		FunctionsThatTakeIdeas.query(saved)
		FunctionsThatTakeIdeas.duration(saved) // fails
		
		val closed = saved.copy(closeDate = Some(new Date()))
		println("closed: " + closed)
		FunctionsThatTakeIdeas.format(closed)
		FunctionsThatTakeIdeas.query(closed)
		FunctionsThatTakeIdeas.duration(closed)
	}
}

