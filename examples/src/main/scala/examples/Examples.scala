/*
 * Copyright 2017 Vitor S. Vieira
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

package com.vitorsvieira.dilate

/**
 * This example uses @valueclass to provide type-safety and some `zero runtime allocation`.
 * The @valueclass macro has no restrictions regarding default argument values!
 *
 * Please refer to the readme for more information.
 */
@valueclass case class BankAccountWithValueclass(
    activated:     Boolean         = true,
    number:        BigInt,
    funds:         BigDecimal,
    withdrawals:   Seq[BigDecimal],
    token:         java.util.UUID,
    @hold manager: String) {

  val classField = "value"
  def classMethod: BigDecimal = funds * 1000
}

object BankAccountWithValueclass {

  val field = "value"
  def renew(bankAccount: BankAccountWithValueclass) =
    bankAccount.copy(token = java.util.UUID.randomUUID())
}

/**
 * This example uses @newtype to provide type-safety and `zero runtime allocation`.
 * Notice the import on top of the class.
 * This import is temporarily required to provide default argument values in the class
 * using the implicit conversion generated inside the object.
 *
 * Please refer to the readme for more information.
 */
import BankAccountWithNewtype._
@newtype case class BankAccountWithNewtype(
  activated:     Boolean         = true,
  number:        BigInt,
  funds:         BigDecimal,
  withdrawals:   Seq[BigDecimal],
  token:         java.util.UUID,
  @hold manager: String)

object BankAccountWithNewtype {
  val field: String = "value"
  def renew(account: BankAccountWithNewtype) = account.copy(token = java.util.UUID.randomUUID().token)
}

/**
 * This class is here only for disassembling comparison purposes
 * javap -p BankAccountWithoutMacro
 *
 */
case class BankAccountWithoutMacro(
    activated:   Boolean         = true,
    number:      BigInt,
    funds:       BigDecimal,
    withdrawals: Seq[BigDecimal],
    token:       java.util.UUID,
    manager:     String) {

  val classField = "value"
  def classMethod: BigDecimal = funds * 1000
}

object BankAccountWithoutMacro {

  val field = "value"
  def renew(bankAccount: BankAccountWithoutMacro) =
    bankAccount.copy(token = java.util.UUID.randomUUID())
}

/**
 * This example shows a class using an external value class as an argument
 * and skipping it using @hold annotation.
 *
 * Note in this example that Person uses BankAccount1.Number and BankAccount2.Number.
 *
 * Both types are not absorbed by Person and stay as the way they are.
 * In current implementation types that are already dependent on other type are not absorbed.
 *
 * Please refer to the readme for more information.
 */
case class Age(value: Int) extends AnyVal

@valueclass sealed class Person(
  v1:           Boolean,
  @hold v2:     Age                              = Age(1),
  v3:           Int                              = 1,
  v4:           Int,
  bankAccount1: BankAccountWithValueclass.Number,
  bankAccount2: BankAccountWithNewtype.Number)

/**
 * *
 * ~ cd dilate
 * ~ sbt
 * > project examples
 * > run
 */
object Examples extends App {

  val bankAccount1 = BankAccountWithValueclass(
    number      = BankAccountWithValueclass.Number(123),
    funds       = BankAccountWithValueclass.Funds(123),
    withdrawals = BankAccountWithValueclass.Withdrawals(Seq(123)),
    token       = BankAccountWithValueclass.Token(java.util.UUID.randomUUID()),
    manager     = "Scala"
  )

  println(s"bankAccount1: $bankAccount1")
  //prints: bankAccount1: BankAccountWithValueclass(Activated(true),Number(123),Funds(123),Withdrawals(List(123)),Token(d42906a7-21f0-48bb-acd7-85673541d7ee),Scala)

  /* @newtype requires named arguments.*/
  val bankAccount2 = BankAccountWithNewtype(
    number      = BigInt(10).number,
    funds       = BigDecimal(10).funds,
    withdrawals = Seq(BigDecimal(10)).withdrawals,
    token       = java.util.UUID.randomUUID().token,
    manager     = "test"
  )
  //Example of how to use the type conversion
  val isActivated: BankAccountWithNewtype.Activated = true.activated
  val number: BankAccountWithNewtype.Number = BigInt(10).number
  val funds: BankAccountWithNewtype.Funds = BigDecimal(10).funds
  val withdrawals: BankAccountWithNewtype.Withdrawals = Seq(BigDecimal(10)).withdrawals
  val token: BankAccountWithNewtype.Token = java.util.UUID.randomUUID().token

  println(s"bankAccount2: $bankAccount2")
  //prints: bankAccount2: BankAccountWithNewtype(true,10,10,List(10),bfbcd8e2-8a65-43ee-a5e0-5fdf9f93ccde,test)
}
