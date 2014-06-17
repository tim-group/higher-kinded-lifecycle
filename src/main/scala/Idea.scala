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

Some simple type definitions and methods in a companion object provide
convenient names and factories for objects in each of the lifecycle stages
which make them usable in much the same way as fully-fledged case classes.
Further type definitions give names to sets of lifecycle stages which have some
coherent meaning. Finally, a couple of implicit conversions ease the
interconversion of values and Somes. Outside the factory methods, this is
principally useful in code which reads the fields declared with higher-kinded
types, as it gets rid of boilerplate Option::get calls.

None of the syntactic sugar in the companion object is necessary, and any bit
of it can be used in isolation from any other bit.
*/

case class Idea[+IfSaved[_] <: Option[_], +IfClosed[_] <: Option[_]](id: IfSaved[String], stock: String, openDate: Date, closeDate: IfClosed[Date])

object Idea {
	type None_[T] = Option[Nothing] // long story
	
	type UnsavedIdea = Idea[None_,  None_]
	type SavedIdea = Idea[Some, None_]
	type ClosedIdea = Idea[Some, Some]
	
	implicit def thing2Some[T](t: T): Some[T] = Some(t)
	implicit def some2Thing[T](s: Some[T]): T = s.get
	
	def UnsavedIdea(stock: String, openDate: Date): UnsavedIdea = Idea(None: None_[String], stock, openDate, None: None_[Date])
	def SavedIdea(id: String, stock: String, openDate: Date): SavedIdea = Idea(id, stock, openDate, None: None_[Date])
	def ClosedIdea(id: String, stock: String, openDate: Date, closeDate: Date): ClosedIdea = Idea(id, stock, openDate, closeDate)
	
	type AnyIdea = Idea[Option, Option]
	type AnySavedIdea = Idea[Some, Option]
	type AnyClosedIdea = Idea[Option, Some]
}

object FunctionsThatTakeIdeas {
	import Idea._
	
	// something that does not require any additional fields
	def format(idea: AnyIdea): String = {
		idea.stock + "@" + idea.openDate
	}
	
	// something that requires an ID
	def query(idea: AnySavedIdea): String = {
		idea.id
	}
	
	// something that requires a close date
	def duration(idea: AnyClosedIdea): Long = {
		idea.closeDate.getTime - idea.openDate.getTime
	}
}

object Main {
	def main(args: Array[String]): Unit = {
		import Idea._
		
		val unsaved: UnsavedIdea = UnsavedIdea("VOD.L", new Date())
		println("unsaved: " + unsaved)
		FunctionsThatTakeIdeas.format(unsaved)
		// FunctionsThatTakeIdeas.query(unsaved) // forbidden
		// FunctionsThatTakeIdeas.duration(unsaved) // forbidden
		
		val saved: SavedIdea = SavedIdea("beefc4c3", "VOD.L", new Date())
		println("saved: " + saved)
		FunctionsThatTakeIdeas.format(saved)
		FunctionsThatTakeIdeas.query(saved)
		// FunctionsThatTakeIdeas.duration(saved) // forbidden
		
		val closed: ClosedIdea = ClosedIdea("beefc4c3", "VOD.L", new Date(), new Date())
		println("closed: " + closed)
		FunctionsThatTakeIdeas.format(closed)
		FunctionsThatTakeIdeas.query(closed)
		FunctionsThatTakeIdeas.duration(closed)
	}
}

