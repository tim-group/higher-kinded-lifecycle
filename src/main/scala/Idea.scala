import java.util.Date

/*
An idea starts life as an unsaved idea, with just a stock and an open date.
When it gets saved, it becomes a saved idea, which also has an ID. When it gets
closed, it becomes a closed idea, which also has a close date.

A way we came up with to model this is with separate classes for each stage in
the lifecycle. In each class, there is a reference to an instance of the class
for the previous stage, and also the fields introduced in that stage.

This offers compile-time protection against trying to perform operations on
ideas which are in the wrong stage of their lifecycle. It does not require code
duplication. However, it is rather clumsy to use.

This is similar to how struct inheritance works in Go, as it happens.
*/

case class UnsavedIdea(stock: String, openDate: Date)
case class SavedIdea(id: String, parent: UnsavedIdea)
case class ClosedIdea(parent: SavedIdea, closeDate: Date)

object FunctionsThatTakeIdeas {
	// something that does not require any additional fields
	def format(idea: UnsavedIdea): String = {
		idea.stock + "@" + idea.openDate
	}
	
	// something that requires an ID
	def query(idea: SavedIdea): String = {
		idea.id
	}
	
	// something that requires a close date
	def duration(idea: ClosedIdea): Long = {
		idea.closeDate.getTime - idea.parent.parent.openDate.getTime
	}
}

object Main {
	def main(args: Array[String]): Unit = {
		val unsaved = UnsavedIdea("VOD.L", new Date())
		println("unsaved: " + unsaved)
		FunctionsThatTakeIdeas.format(unsaved)
		// FunctionsThatTakeIdeas.query(unsaved) // forbidden
		// FunctionsThatTakeIdeas.duration(unsaved) // forbidden
		
		val saved = SavedIdea("beefc4c3", unsaved)
		println("saved: " + saved)
		FunctionsThatTakeIdeas.format(saved.parent)
		FunctionsThatTakeIdeas.query(saved)
		// FunctionsThatTakeIdeas.duration(saved) // forbidden
		
		val closed = ClosedIdea(saved, new Date())
		println("closed: " + closed)
		FunctionsThatTakeIdeas.format(closed.parent.parent)
		FunctionsThatTakeIdeas.query(closed.parent)
		FunctionsThatTakeIdeas.duration(closed)
	}
}

