package li.cil.oc.server.driver.item

import li.cil.oc.api
import li.cil.oc.api.driver.EnvironmentAware
import li.cil.oc.api.driver.EnvironmentHost
import li.cil.oc.api.driver.item.HostAware
import li.cil.oc.api.network.Environment
import li.cil.oc.common.Slot
import li.cil.oc.common.Tier
import li.cil.oc.common.init.Items
import li.cil.oc.common.item
import li.cil.oc.common.tileentity.traits.BundledRedstoneAware
import li.cil.oc.common.tileentity.traits.RedstoneAware
import li.cil.oc.server.component
import li.cil.oc.util.mods.BundledRedstone
import li.cil.oc.util.mods.WirelessRedstone
import net.minecraft.item.ItemStack

object RedstoneCard extends Item with HostAware with EnvironmentAware {
  override def worksWith(stack: ItemStack) = isOneOf(stack, api.Items.get("redstoneCard1"), api.Items.get("redstoneCard2"))

  override def worksWith(stack: ItemStack, host: Class[_ <: EnvironmentHost]) =
    worksWith(stack) && isComputer(host)

  override def createEnvironment(stack: ItemStack, host: EnvironmentHost) =
    host match {
      case redstone: BundledRedstoneAware if BundledRedstone.isAvailable && tier(stack) == Tier.Two =>
        if (WirelessRedstone.isAvailable) new RedstoneBundledWireless(redstone)
        else new RedstoneBundled(redstone)
      case redstone: RedstoneAware =>
        if (tier(stack) == Tier.Two && WirelessRedstone.isAvailable) new RedstoneWireless(redstone)
        else new component.Redstone[RedstoneAware](redstone)
      case _ => null
    }

  override def slot(stack: ItemStack) = Slot.Card

  override def tier(stack: ItemStack) =
    Items.multi.subItem(stack) match {
      case Some(card: item.RedstoneCard) => card.tier
      case _ => Tier.One
    }

  override def providedEnvironment(stack: ItemStack): Class[_ <: Environment] =
    if (stack.getItemDamage == api.Items.get("redstoneCard1").createItemStack(1).getItemDamage)
      classOf[component.Redstone[RedstoneAware]]
    else if (BundledRedstone.isAvailable) {
      if (WirelessRedstone.isAvailable) classOf[RedstoneBundledWireless]
      else classOf[RedstoneBundled]
    }
    else {
      if (WirelessRedstone.isAvailable) classOf[RedstoneWireless]
      else classOf[component.Redstone[RedstoneAware]]
    }
}

class RedstoneBundled(redstone: BundledRedstoneAware) extends component.Redstone[BundledRedstoneAware](redstone) with component.RedstoneBundled

class RedstoneWireless(redstone: RedstoneAware) extends component.Redstone[RedstoneAware](redstone) with component.RedstoneWireless

class RedstoneBundledWireless(redstone: BundledRedstoneAware) extends RedstoneBundled(redstone) with component.RedstoneWireless
