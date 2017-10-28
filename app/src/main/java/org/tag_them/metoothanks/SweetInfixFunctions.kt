package org.tag_them.metoothanks

import org.tag_them.metoothanks.items.Item

infix fun <T> T.prnt(to_print: Any) = print(to_print)

fun hey() {
	prnt "A"
}
