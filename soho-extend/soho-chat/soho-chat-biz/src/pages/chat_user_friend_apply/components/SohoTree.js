import React, { PureComponent } from 'react'
import {Table, Avatar, Modal, Tooltip, Typography, Tree, Input} from 'antd'
import PropTypes from "prop-types";
import { DownOutlined, PlusOutlined, CloseOutlined, EditOutlined } from '@ant-design/icons';
import {addRootNode} from "../../../utils/tree";
const { confirm } = Modal
const { Text } = Typography;
const { TreeNode } = Tree;

class SohoTree extends PureComponent {
  map = {}
  parentList = []

  constructor(props) {
    super(props);
  }

  //编辑选中节点
  onEdit = (key) => {
    const { onEditItem } = this.props
    onEditItem(this.map[key])
  };

  //删除选中节点
  onDelete = (key) => {
    const { onDeleteItem } = this.props
    onDeleteItem(key)
  };

  //添加选中节点子结点
  onAdd = (key) => {
    const { onChildItem } = this.props
    onChildItem({parentId: key})
  };

  onTitleRender = (item) => {
    return (
      <div style={{ display: 'flex', alignItems: 'center', height:30 }}>

        <span>
          {item.title}
        </span>
        <span style={{ display: 'flex', marginLeft: 30 }}>
          <PlusOutlined style={{ marginLeft: 10 }} onClick={() => this.onAdd(item.key)} />
          {item.key != 0 &&
            <>
              <EditOutlined style={{marginLeft: 10}} onClick={() => this.onEdit(item.key)}/>
              <CloseOutlined style={{marginLeft: 10}} onClick={() => this.onDelete(item.key)}/>
            </>
          }
        </span>
      </div>
    );
  };

  loadTree = (parentId, map, parentList) => {
    let sons = []
    if(parentList[parentId] == null) {
      return sons;
    }
    parentList[parentId].map((item)=>{
      //检查是否有子结点
      let currentData = {
        key: item.id,
        title: item.title
      }
      if(parentList[item.id] != null) {
        currentData['children'] = this.loadTree(item.id, map, parentList)
      }
      sons.push(currentData)
    })
    return sons;
  }

  loadDataFromList = (list) => {
    let map = {}
    let parentList = {}
    list.map((item)=>{
      map[item.id] = item
      if(parentList[item.parentId] == null) {
        parentList[item.parentId] = []
      }
      parentList[item.parentId].push(item)
    })

    return {map: map, parentList: parentList}
    // return this.loadTree(0, map, parentList)
  }

  render() {
    const { options,dataSource,...tableProps } = this.props
    const {map, parentList} = this.loadDataFromList(dataSource)
    this.map = map
    this.parentList = parentList
    return (
      <div style={{margin:'30 px'}}>
        <Tree
          showLine
          switcherIcon={<DownOutlined />}
          treeData={addRootNode(this.loadTree(0, map, parentList))}
          defaultExpandAll={true}
          titleRender={this.onTitleRender}
        />
      </div>

    )
  }
}

SohoTree.propTypes = {
  onDeleteItem: PropTypes.func,
  onEditItem: PropTypes.func,
  location: PropTypes.object,
}

export default SohoTree