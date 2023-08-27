import React, { PureComponent } from 'react'
import PropTypes from 'prop-types'
import {connect, history} from 'umi'
import { stringify } from 'qs'
import { t } from "@lingui/macro"
import { Page } from 'components'
import List from './components/List'
import ChatUserFriendApplyModal from "./components/Modal";
import Filter from "./components/Filter"


@connect(({ chat_user_friend_apply, loading }) => ({ chat_user_friend_apply, loading }))
class ChatUserFriendApply extends PureComponent {
  handleRefresh = newQuery => {
    const { location } = this.props
    const { query, pathname } = location
    if(newQuery && newQuery.createTime) {
      newQuery.startDate = newQuery.createTime[0]
      newQuery.endDate = newQuery.createTime[1]
      delete newQuery.createTime
    }
    history.push({
      pathname,
      search: stringify(
        {
          ...query,
          ...newQuery,
        },
        { arrayFormat: 'repeat' }
      ),
    })
  }
  handleTabClick = key => {
    const { pathname } = this.props.location

    history.push({
      pathname,
      search: stringify({
        status: key,
      }),
    })
  }

  get listProps() {
    const { chat_user_friend_apply, loading, location, dispatch } = this.props
    const { list, pagination } = chat_user_friend_apply
    const { query, pathname } = location
    return {
      options: chat_user_friend_apply.options,
      trees: chat_user_friend_apply.trees,
      pagination,
      dataSource: list,
      loading: loading.effects['chat_user_friend_apply/query'],
      onChange(page) {
        history.push({
          pathname,
          search: stringify({
            ...query,
            page: page.current,
            pageSize: page.pageSize,
          }),
        })
      },
      onDeleteItem: id => {
        dispatch({
          type: 'chat_user_friend_apply/delete',
          payload: id,
        }).then(() => {
          this.handleRefresh({
            page:
              list.length === 1 && pagination.current > 1
                ? pagination.current - 1
                : pagination.current,
          })
        })
      },
      onEditItem(item) {
        dispatch({
          type: 'chat_user_friend_apply/showModal',
          payload: {
            modalType: 'update',
            currentItem: item,
          },
        })
      },
      onApplyItem(item) {
        dispatch({
          type: 'chat_user_friend_apply/showModal',
          payload: {
            modalType: 'apply',
            currentItem: item,
          }
        })
      },
      onChildItem(item) {
        dispatch({
          type: 'chat_user_friend_apply/showModal',
          payload: {
            modalType: 'create',
            currentItem: {...item},
          },
        })
      },    }
  }

  get modalProps() {
    const { dispatch, chat_user_friend_apply, loading } = this.props
    const { currentItem, modalVisible, modalType,resourceTree,resourceIds } = chat_user_friend_apply

    return {
      options: chat_user_friend_apply.options,
      trees: chat_user_friend_apply.trees,
      item: modalType === 'create' ? {...currentItem} : currentItem,
      visible: modalVisible,
      destroyOnClose: true,
      maskClosable: false,
      confirmLoading: loading.effects[`chat_user_friend_apply/${modalType}`],
      resourceTree: resourceTree,
      resourceIds: resourceIds,
      title: `${
          modalType === 'create' ? t`Create` : (modalType === 'update' ? t`Update` : t`Apply`)
      }`,
      centered: true,
      onOk: data => {
        dispatch({
          type: `chat_user_friend_apply/${modalType}`,
          payload: data,
        }).then(() => {
          this.handleRefresh()
        })
      },
      onCancel() {
        dispatch({
          type: 'chat_user_friend_apply/hideModal',
        })
      },
    }
  }

  get filterProps() {
    const { location, dispatch, chat_user_friend_apply} = this.props
    const {query,pathname} = location
    return {
      options: chat_user_friend_apply.options,
      trees: chat_user_friend_apply.trees,
      filter: {
        ...query,
      },
      onFilterChange(values) {
        history.push({
          pathname,
          search: stringify(values),
        })
      },
      onAdd() {
        dispatch({
          type: 'chat_user_friend_apply/showModal',
          payload: {
            modalType: 'create',
            currentItem: {},
          },
        })
      },
    }
  }

  render() {
    return (
      <Page inner>

        <Filter {...this.filterProps} />
        <List {...this.listProps} />
        <ChatUserFriendApplyModal {...this.modalProps} />
      </Page>
    )
  }
}

ChatUserFriendApply.propTypes = {
  chat_user_friend_apply: PropTypes.object,
  loading: PropTypes.object,
  location: PropTypes.object,
  dispatch: PropTypes.func,
}

export default ChatUserFriendApply
