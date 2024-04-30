import modelExtend from 'dva-model-extend'
import api from 'api'
import { pageModel } from 'utils/model'
const { pathToRegexp } = require("path-to-regexp")
const { queryTemporalCategoryList, updateTemporalCategory, createTemporalCategory,deleteTemporalCategory,queryTemporalCategoryTree } = api
/**
 *
   //相关api信息
 queryTemporalCategoryList: `GET ${prefix}/admin/temporalCategory/list`,
 updateTemporalCategory: `PUT ${prefix}/admin/temporalCategory`,
 createTemporalCategory: `POST ${prefix}/admin/temporalCategory`,
 deleteTemporalCategory: `DELETE ${prefix}/admin/temporalCategory/:ids`,
 queryTemporalCategoryDetails: `GET ${prefix}/admin/temporalCategory/:id`,
 *
 */
export default modelExtend(pageModel, {
  namespace: 'temporal_category',
  state: {
    optionValueLabel: {},    options: {    },
    trees: {
           parentId: [],
    },
  },

  subscriptions: {
    setup({ dispatch, history }) {
      history.listen(location => {
        if (pathToRegexp('/temporal_category').exec(location.pathname)) {
          dispatch({
            type: 'query',
            payload: {
              pageSize: 20,
              page: 1,
              ...location.query,
            },
          })
          dispatch({
            type: 'queryTemporalCategoryTree',
            payload: {},
          })
        }
      })
    },
  },

  effects: {
    *query({ payload }, { call, put }) {
      const data = yield call(queryTemporalCategoryList, payload)
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
      const data = yield call(createTemporalCategory, payload)
      if (data.success) {
        yield put({ type: 'hideModal' })
      } else {
        throw data
      }
    },

    *update({ payload }, { select, call, put }) {
      const data = yield call(updateTemporalCategory, payload)
      if (data.success) {
        yield put({ type: 'hideModal' })
      } else {
        throw data
      }
    },
    *delete({ payload }, { select, call, put }) {
      const data = yield call(deleteTemporalCategory, {ids:payload})
      if (!data.success) {
        throw data
      }
    },
   *queryTemporalCategoryTree({payload}, {call, put}) {
      const data = yield call(queryTemporalCategoryTree, payload);
      if(data.success) {
        yield put({
          type: 'querySuccessTree',
          payload: {
            parentId: data.payload,
          }
        })
      } else {
        throw data;
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