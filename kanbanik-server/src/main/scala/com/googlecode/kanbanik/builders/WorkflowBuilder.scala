package com.googlecode.kanbanik.builders

import com.googlecode.kanbanik.model.Workflow
import com.googlecode.kanbanik.model.Board
import com.googlecode.kanbanik.dtos.{BoardDto, WorkflowDto}
import org.bson.types.ObjectId

class WorkflowBuilder extends BaseBuilder {

  def buildEntity(workflow: WorkflowDto, board: Option[Board]): Workflow = {
    val res = new Workflow(
    {
      if (workflow.id != null && workflow.id != "" && workflow.id.isDefined) {
        Some(new ObjectId(workflow.id.get))
      } else {
        None
      }
    },
    workflow.workflowitems.getOrElse(List()).map(item => workflowitemBuilder.buildEntity(item, None, board)),
    board
    )

    res.copy(workflowitems = res.workflowitems.map(_.copy(_parentWorkflow = Some(res))))
  }

  def buildShallowDto(workflow: Workflow, board: Option[BoardDto]) = {
    WorkflowDto(
      Some(workflow.id.get.toString),
      None,
      board.getOrElse(boardBuilder.buildShallowDto(workflow.board))
    )

  }

  def buildDto(workflow: Workflow, board: Option[BoardDto]) = {
    val res = buildShallowDto(workflow, board)
    val workflowitems = workflow.workflowitems.map(workflowitemBuilder.buildDto(_, Some(res)))
    res.copy(workflowitems = Some(workflowitems))
  }

  def workflowitemBuilder = new WorkflowitemBuilder

  def boardBuilder = new BoardBuilder
}