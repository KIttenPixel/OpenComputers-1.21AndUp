package li.cil.oc.server.driver.item

import li.cil.oc.api.driver.{Container, Slot}
import li.cil.oc.util.ItemUtils
import li.cil.oc.{Settings, api}
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.common.util.Constants.NBT

object Tablet extends Item {
  override def worksWith(stack: ItemStack) = isOneOf(stack, api.Items.get("tablet"))

  override def createEnvironment(stack: ItemStack, container: Container) = {
    val data = new ItemUtils.TabletData(stack)
    data.items.collect {
      case Some(fs) if FileSystem.worksWith(fs) => fs
    }.headOption.map(FileSystem.createEnvironment(_, container)).orNull
  }

  override def slot(stack: ItemStack) = Slot.None

  override def dataTag(stack: ItemStack) = {
    val data = new ItemUtils.TabletData(stack)
    val index = data.items.indexWhere {
      case Some(fs) => FileSystem.worksWith(fs)
      case _ => false
    }
    if (index >= 0 && stack.hasTagCompound && stack.getTagCompound.hasKey(Settings.namespace + "items")) {
      val baseTag = stack.getTagCompound.getTagList(Settings.namespace + "items", NBT.TAG_COMPOUND).getCompoundTagAt(index)
      if (!baseTag.hasKey("item")) {
        baseTag.setTag("item", new NBTTagCompound())
      }
      val itemTag = baseTag.getCompoundTag("item")
      if (!itemTag.hasKey("tag")) {
        itemTag.setTag("tag", new NBTTagCompound())
      }
      val stackTag = itemTag.getCompoundTag("tag")
      if (!stackTag.hasKey(Settings.namespace + "data")) {
        stackTag.setTag(Settings.namespace + "data", new NBTTagCompound())
      }
      stackTag.getCompoundTag(Settings.namespace + "data")
    }
    else new NBTTagCompound()
  }
}