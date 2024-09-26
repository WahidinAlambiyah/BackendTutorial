<template>
  <div>
    <h2 id="page-heading" data-cy="TrxEventHeading">
      <span v-text="t$('monolithApp.trxEvent.home.title')" id="trx-event-heading"></span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" v-on:click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="t$('monolithApp.trxEvent.home.refreshListLabel')"></span>
        </button>
        <router-link :to="{ name: 'TrxEventCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-trx-event"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="t$('monolithApp.trxEvent.home.createLabel')"></span>
          </button>
        </router-link>
      </div>
    </h2>
    <div class="row">
      <div class="col-sm-12">
        <form name="searchForm" class="form-inline" v-on:submit.prevent="search(currentSearch)">
          <div class="input-group w-100 mt-3">
            <input
              type="text"
              class="form-control"
              name="currentSearch"
              id="currentSearch"
              v-bind:placeholder="t$('monolithApp.trxEvent.home.search')"
              v-model="currentSearch"
            />
            <button type="button" id="launch-search" class="btn btn-primary" v-on:click="search(currentSearch)">
              <font-awesome-icon icon="search"></font-awesome-icon>
            </button>
            <button type="button" id="clear-search" class="btn btn-secondary" v-on:click="clear()" v-if="currentSearch">
              <font-awesome-icon icon="trash"></font-awesome-icon>
            </button>
          </div>
        </form>
      </div>
    </div>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && trxEvents && trxEvents.length === 0">
      <span v-text="t$('monolithApp.trxEvent.home.notFound')"></span>
    </div>
    <div class="table-responsive" v-if="trxEvents && trxEvents.length > 0">
      <table class="table table-striped" aria-describedby="trxEvents">
        <thead>
          <tr>
            <th scope="row"><span v-text="t$('global.field.id')"></span></th>
            <th scope="row"><span v-text="t$('monolithApp.trxEvent.title')"></span></th>
            <th scope="row"><span v-text="t$('monolithApp.trxEvent.description')"></span></th>
            <th scope="row"><span v-text="t$('monolithApp.trxEvent.date')"></span></th>
            <th scope="row"><span v-text="t$('monolithApp.trxEvent.location')"></span></th>
            <th scope="row"><span v-text="t$('monolithApp.trxEvent.capacity')"></span></th>
            <th scope="row"><span v-text="t$('monolithApp.trxEvent.price')"></span></th>
            <th scope="row"><span v-text="t$('monolithApp.trxEvent.status')"></span></th>
            <th scope="row"><span v-text="t$('monolithApp.trxEvent.service')"></span></th>
            <th scope="row"><span v-text="t$('monolithApp.trxEvent.testimonial')"></span></th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="trxEvent in trxEvents" :key="trxEvent.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'TrxEventView', params: { trxEventId: trxEvent.id } }">{{ trxEvent.id }}</router-link>
            </td>
            <td>{{ trxEvent.title }}</td>
            <td>{{ trxEvent.description }}</td>
            <td>{{ formatDateShort(trxEvent.date) || '' }}</td>
            <td>{{ trxEvent.location }}</td>
            <td>{{ trxEvent.capacity }}</td>
            <td>{{ trxEvent.price }}</td>
            <td v-text="t$('monolithApp.EventStatus.' + trxEvent.status)"></td>
            <td>
              <div v-if="trxEvent.service">
                <router-link :to="{ name: 'MstServiceView', params: { mstServiceId: trxEvent.service.id } }">{{
                  trxEvent.service.id
                }}</router-link>
              </div>
            </td>
            <td>
              <div v-if="trxEvent.testimonial">
                <router-link :to="{ name: 'TrxTestimonialView', params: { trxTestimonialId: trxEvent.testimonial.id } }">{{
                  trxEvent.testimonial.id
                }}</router-link>
              </div>
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'TrxEventView', params: { trxEventId: trxEvent.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.view')"></span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'TrxEventEdit', params: { trxEventId: trxEvent.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.edit')"></span>
                  </button>
                </router-link>
                <b-button
                  v-on:click="prepareRemove(trxEvent)"
                  variant="danger"
                  class="btn btn-sm"
                  data-cy="entityDeleteButton"
                  v-b-modal.removeEntity
                >
                  <font-awesome-icon icon="times"></font-awesome-icon>
                  <span class="d-none d-md-inline" v-text="t$('entity.action.delete')"></span>
                </b-button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    <b-modal ref="removeEntity" id="removeEntity">
      <template #modal-title>
        <span id="monolithApp.trxEvent.delete.question" data-cy="trxEventDeleteDialogHeading" v-text="t$('entity.delete.title')"></span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-trxEvent-heading" v-text="t$('monolithApp.trxEvent.delete.question', { id: removeId })"></p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-text="t$('entity.action.cancel')" v-on:click="closeDialog()"></button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-trxEvent"
            data-cy="entityConfirmDeleteButton"
            v-text="t$('entity.action.delete')"
            v-on:click="removeTrxEvent()"
          ></button>
        </div>
      </template>
    </b-modal>
  </div>
</template>

<script lang="ts" src="./trx-event.component.ts"></script>
