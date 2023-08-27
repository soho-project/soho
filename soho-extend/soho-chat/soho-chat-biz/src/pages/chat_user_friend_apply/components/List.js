import React, { PureComponent } from 'react'
import {Table, Avatar, Modal, Tooltip, Typography} from 'antd'
import {t, Trans} from "@lingui/macro"
import { Ellipsis } from 'components'
import {DropOption} from "../../../components";
import PropTypes from "prop-types";
import {getNavigationTreeTitle} from "../../../utils/tree";
const { confirm } = Modal
const { Text } = Typography;

class List extends PureComponent {
  handleMenuClick = (record, e) => {
    const { onDeleteItem, onEditItem  }= this.props

    if (e.key === '1') {
      onEditItem(record)
    } else if (e.key === '2') {
      confirm({
        title: t`Are you sure delete this record?`,
        onOk() {
          onDeleteItem(record.id)
        },
      })
    }
  }

  render() {
    const { options,trees,...tableProps } = this.props
    const columns = [
      {
        title: t`Id`,
        dataIndex: 'id',
        key: 'id',
      },
      {
        title: t`Chat Uid`,
        dataIndex: 'chatUid',
        key: 'chatUid',
      },
      {
        title: t`Friend Uid`,
        dataIndex: 'friendUid',
        key: 'friendUid',
      },
      {
        title: t`Status`,
        dataIndex: 'status',
        key: 'status',
      },
      {
        title: t`Ask`,
        dataIndex: 'ask',
        key: 'ask',
           render:(text, recode) => {
          if(text != null && text.length>60) {
            const start = text.slice(0, 120).trim();
            return <Text style={{ maxWidth: '100%' }} ellipsis={ "..." }>{start}</Text>
          } else {
            return text
          }
        }
      },
      {
        title: t`Answer`,
        dataIndex: 'answer',
        key: 'answer',
           render:(text, recode) => {
          if(text != null && text.length>60) {
            const start = text.slice(0, 120).trim();
            return <Text style={{ maxWidth: '100%' }} ellipsis={ "..." }>{start}</Text>
          } else {
            return text
          }
        }
      },
      {
        title: t`Updated Time`,
        dataIndex: 'updatedTime',
        key: 'updatedTime',
      },
      {
        title: t`Created Time`,
        dataIndex: 'createdTime',
        key: 'createdTime',
      },
      {
        title: <Trans>Operation</Trans>,
        key: 'operation',
        fixed: 'right',
        width: '8%',
        render: (text, record) => {
          return (
            <DropOption
              onMenuClick={e => this.handleMenuClick(record, e)}
              menuOptions={[
                { key: '1', name: t`Update` },
                { key: '2', name: t`Delete` },
              ]}
            />
          )
        },
      },
    ]

    return (
      <Table
        {...tableProps}
        pagination={{
          ...tableProps.pagination,
          showTotal: (total)=>{return t`Total ` + total + t` Items`},
        }}
        bordered
        scroll={{ x: 1200 }}
        columns={columns}
        simple
        rowKey={record => record.id}
      />
    )
  }
}

List.propTypes = {
  onDeleteItem: PropTypes.func,
  onEditItem: PropTypes.func,
  location: PropTypes.object,
}

export default List
