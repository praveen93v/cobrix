/*
 * Copyright 2018 ABSA Group Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package za.co.absa.cobrix.spark.cobol.reader.rules.parsing.impl

import za.co.absa.cobrix.spark.cobol.reader.rules.evaluation.RuleEvaluatorFactory
import za.co.absa.cobrix.spark.cobol.reader.rules.impls.SimpleNashornRule
import za.co.absa.cobrix.spark.cobol.reader.rules.language.Tokens
import za.co.absa.cobrix.spark.cobol.reader.rules.parsing.RulesParserTemplate
import za.co.absa.cobrix.spark.cobol.reader.rules.{Rule, RuleExpression}

/**
  * This class implements an expression parser based on Oracle's Nashorn JavaScript engine.
  *
  * However Nashorn is going to be deprecated (see JEP 335).
  */
private[rules] class NashornRuleParser extends RulesParserTemplate {

  override def parseRuleExpression(ruleExpression: RuleExpression): Rule = {

    val fields = Tokens.getFieldsFromExpression(ruleExpression.expression)
    val cleanExpression = Tokens.cleanExpressionFields(ruleExpression.expression)

    new SimpleNashornRule(fields, new RuleExpression(cleanExpression, ruleExpression.result), RuleEvaluatorFactory.buildNashornEvaluator())
  }
}