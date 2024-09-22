<template>
  <div>
    <h2 id="page-heading" data-cy="ServiceHeading">
      <span v-text="t$('jHipsterMonolithApp.service.home.title')" id="service-heading"></span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" v-on:click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="t$('jHipsterMonolithApp.service.home.refreshListLabel')"></span>
        </button>
        <router-link :to="{ name: 'ServiceCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-service"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="t$('jHipsterMonolithApp.service.home.createLabel')"></span>
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
              v-bind:placeholder="t$('jHipsterMonolithApp.service.home.search')"
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
    <div class="alert alert-warning" v-if="!isFetching && services && services.length === 0">
      <span v-text="t$('jHipsterMonolithApp.service.home.notFound')"></span>
    </div>
    <div class="table-responsive" v-if="services && services.length > 0">
      <table class="table table-striped" aria-describedby="services">
        <thead>
          <tr>
            <th scope="row"><span v-text="t$('global.field.id')"></span></th>
            <th scope="row"><span v-text="t$('jHipsterMonolithApp.service.name')"></span></th>
            <th scope="row"><span v-text="t$('jHipsterMonolithApp.service.description')"></span></th>
            <th scope="row"><span v-text="t$('jHipsterMonolithApp.service.price')"></span></th>
            <th scope="row"><span v-text="t$('jHipsterMonolithApp.service.durationInHours')"></span></th>
            <th scope="row"><span v-text="t$('jHipsterMonolithApp.service.serviceType')"></span></th>
            <th scope="row"><span v-text="t$('jHipsterMonolithApp.service.testimonial')"></span></th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="service in services" :key="service.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'ServiceView', params: { serviceId: service.id } }">{{ service.id }}</router-link>
            </td>
            <td>{{ service.name }}</td>
            <td>{{ service.description }}</td>
            <td>{{ service.price }}</td>
            <td>{{ service.durationInHours }}</td>
            <td v-text="t$('jHipsterMonolithApp.ServiceType.' + service.serviceType)"></td>
            <td>
              <div v-if="service.testimonial">
                <router-link :to="{ name: 'TestimonialView', params: { testimonialId: service.testimonial.id } }">{{
                  service.testimonial.id
                }}</router-link>
              </div>
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'ServiceView', params: { serviceId: service.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.view')"></span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'ServiceEdit', params: { serviceId: service.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.edit')"></span>
                  </button>
                </router-link>
                <b-button
                  v-on:click="prepareRemove(service)"
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
        <span
          id="jHipsterMonolithApp.service.delete.question"
          data-cy="serviceDeleteDialogHeading"
          v-text="t$('entity.delete.title')"
        ></span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-service-heading" v-text="t$('jHipsterMonolithApp.service.delete.question', { id: removeId })"></p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-text="t$('entity.action.cancel')" v-on:click="closeDialog()"></button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-service"
            data-cy="entityConfirmDeleteButton"
            v-text="t$('entity.action.delete')"
            v-on:click="removeService()"
          ></button>
        </div>
      </template>
    </b-modal>
  </div>
</template>

<script lang="ts" src="./service.component.ts"></script>
