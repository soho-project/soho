import modelExtend from 'dva-model-extend'
import api from 'api'
import { pageModel } from 'utils/model'
const { pathToRegexp } = require("path-to-regexp")
const { queryChatUserFriendApplyList, updateChatUserFriendApply, createChatUserFriendApply,deleteChatUserFriendApply } = api
/**
 *
   //相关api信息
 queryChatUserFriendApplyList: `GET ${prefix}/admin/chatUserFriendApply/list`,
 updateChatUserFriendApply: `PUT ${prefix}/admin/chatUserFriendApply`,
 createChatUserFriendApply: `POST ${prefix}/admin/chatUserFriendApply`,
 deleteChatUserFriendApply: `DELETE ${prefix}/admin/chatUserFriendApply/:ids`,
 queryChatUserFriendApplyDetails: `GET ${prefix}/admin/chatUserFriendApply/:id`,
 *
 */
export default modelExtend(pageModel, {
  namespace: 'chat_user_friend_apply',
  state: {
    options: {    },
    trees: {
    },
  },

  subscriptions: {
    setup({ dispatch, history }) {
      history.listen(location => {
        if (pathToRegexp('/chat_user_friend_apply').exec(location.pathname)) {
          dispatch({
            type: 'query',
            payload: {
              pageSize: 20,
              page: 1,
              ...location.query,
            },
          })
        }
      })
    },
  },

  effects: {
    *query({ payload }, { call, put }) {
      const data = yield call(queryChatUserFriendApplyList, payload)
      if (data.success) {
        yield put({
          type: 'querySuccess',
          payload: {
            list: data.payload.list,
            pagination: {
              current: Number(payload.page) || 1,
              pageSize: Number(payload.pageSize) || 10,
              total: data.payload.total,
            },
          },
        })
      } else {
        throw data
      }
    },
   *create({ payload }, { call, put }) {
      const data = yield call(createChatUserFriendApply, payload)
      if (data.success) {
        yield put({ type: 'hideModal' })
      } else {
        throw data
      }
    },

    *update({ payload }, { select, call, put }) {
      const data = yield call(updateChatUserFriendApply, payload)
      if (data.success) {
        yield put({ type: 'hideModal' })
      } else {
        throw data
      }
    },
    *delete({ payload }, { select, call, put }) {
      const data = yield call(deleteChatUserFriendApply, {ids:payload})
      if (!data.success) {
        throw data
      }
    },
  },
  reducers: {
    showModal(state, { payload }) {
      return { ...state, ...payload, modalVisible: true }
    },

    hideModal(state) {
      return { ...state, modalVisible: false }
    },
  },
})