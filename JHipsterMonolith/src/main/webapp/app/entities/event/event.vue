<template>
  <div>
    <h2 id="page-heading" data-cy="EventHeading">
      <span v-text="t$('jHipsterMonolithApp.event.home.title')" id="event-heading"></span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" v-on:click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="t$('jHipsterMonolithApp.event.home.refreshListLabel')"></span>
        </button>
        <router-link :to="{ name: 'EventCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-event"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="t$('jHipsterMonolithApp.event.home.createLabel')"></span>
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
              v-bind:placeholder="t$('jHipsterMonolithApp.event.home.search')"
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
    <div class="alert alert-warning" v-if="!isFetching && events && events.length === 0">
      <span v-text="t$('jHipsterMonolithApp.event.home.notFound')"></span>
    </div>
    <div class="table-responsive" v-if="events && events.length > 0">
      <table class="table table-striped" aria-describedby="events">
        <thead>
          <tr>
            <th scope="row"><span v-text="t$('global.field.id')"></span></th>
            <th scope="row"><span v-text="t$('jHipsterMonolithApp.event.title')"></span></th>
            <th scope="row"><span v-text="t$('jHipsterMonolithApp.event.description')"></span></th>
            <th scope="row"><span v-text="t$('jHipsterMonolithApp.event.date')"></span></th>
            <th scope="row"><span v-text="t$('jHipsterMonolithApp.event.location')"></span></th>
            <th scope="row"><span v-text="t$('jHipsterMonolithApp.event.capacity')"></span></th>
            <th scope="row"><span v-text="t$('jHipsterMonolithApp.event.price')"></span></th>
            <th scope="row"><span v-text="t$('jHipsterMonolithApp.event.status')"></span></th>
            <th scope="row"><span v-text="t$('jHipsterMonolithApp.event.service')"></span></th>
            <th scope="row"><span v-text="t$('jHipsterMonolithApp.event.testimonial')"></span></th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="event in events" :key="event.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'EventView', params: { eventId: event.id } }">{{ event.id }}</router-link>
            </td>
            <td>{{ event.title }}</td>
            <td>{{ event.description }}</td>
            <td>{{ formatDateShort(event.date) || '' }}</td>
            <td>{{ event.location }}</td>
            <td>{{ event.capacity }}</td>
            <td>{{ event.price }}</td>
            <td v-text="t$('jHipsterMonolithApp.EventStatus.' + event.status)"></td>
            <td>
              <div v-if="event.service">
                <router-link :to="{ name: 'ServiceView', params: { serviceId: event.service.id } }">{{ event.service.id }}</router-link>
              </div>
            </td>
            <td>
              <div v-if="event.testimonial">
                <router-link :to="{ name: 'TestimonialView', params: { testimonialId: event.testimonial.id } }">{{
                  event.testimonial.id
                }}</router-link>
              </div>
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'EventView', params: { eventId: event.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.view')"></span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'EventEdit', params: { eventId: event.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.edit')"></span>
                  </button>
                </router-link>
                <b-button
                  v-on:click="prepareRemove(event)"
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
        <span id="jHipsterMonolithApp.event.delete.question" data-cy="eventDeleteDialogHeading" v-text="t$('entity.delete.title')"></span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-event-heading" v-text="t$('jHipsterMonolithApp.event.delete.question', { id: removeId })"></p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-text="t$('entity.action.cancel')" v-on:click="closeDialog()"></button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-event"
            data-cy="entityConfirmDeleteButton"
            v-text="t$('entity.action.delete')"
            v-on:click="removeEvent()"
          ></button>
        </div>
      </template>
    </b-modal>
  </div>
</template>

<script lang="ts" src="./event.component.ts"></script>
