<template>
  <div>
    <h2 id="page-heading" data-cy="CountryHeading">
      <span v-text="t$('jHipsterMonolithApp.country.home.title')" id="country-heading"></span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" v-on:click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="t$('jHipsterMonolithApp.country.home.refreshListLabel')"></span>
        </button>
        <router-link :to="{ name: 'CountryCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-country"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="t$('jHipsterMonolithApp.country.home.createLabel')"></span>
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
              v-bind:placeholder="t$('jHipsterMonolithApp.country.home.search')"
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
    <div class="alert alert-warning" v-if="!isFetching && countries && countries.length === 0">
      <span v-text="t$('jHipsterMonolithApp.country.home.notFound')"></span>
    </div>
    <div class="table-responsive" v-if="countries && countries.length > 0">
      <table class="table table-striped" aria-describedby="countries">
        <thead>
          <tr>
            <th scope="row" v-on:click="changeOrder('id')">
              <span v-text="t$('global.field.id')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'id'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('name')">
              <span v-text="t$('jHipsterMonolithApp.country.name')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'name'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('unm49Code')">
              <span v-text="t$('jHipsterMonolithApp.country.unm49Code')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'unm49Code'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('isoAlpha2Code')">
              <span v-text="t$('jHipsterMonolithApp.country.isoAlpha2Code')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'isoAlpha2Code'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('region.name')">
              <span v-text="t$('jHipsterMonolithApp.country.region')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'region.name'"></jhi-sort-indicator>
            </th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="country in countries" :key="country.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'CountryView', params: { countryId: country.id } }">{{ country.id }}</router-link>
            </td>
            <td>{{ country.name }}</td>
            <td>{{ country.unm49Code }}</td>
            <td>{{ country.isoAlpha2Code }}</td>
            <td>
              <div v-if="country.region">
                <router-link :to="{ name: 'RegionView', params: { regionId: country.region.id } }">{{ country.region.name }}</router-link>
              </div>
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'CountryView', params: { countryId: country.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.view')"></span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'CountryEdit', params: { countryId: country.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.edit')"></span>
                  </button>
                </router-link>
                <b-button
                  v-on:click="prepareRemove(country)"
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
          id="jHipsterMonolithApp.country.delete.question"
          data-cy="countryDeleteDialogHeading"
          v-text="t$('entity.delete.title')"
        ></span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-country-heading" v-text="t$('jHipsterMonolithApp.country.delete.question', { id: removeId })"></p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-text="t$('entity.action.cancel')" v-on:click="closeDialog()"></button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-country"
            data-cy="entityConfirmDeleteButton"
            v-text="t$('entity.action.delete')"
            v-on:click="removeCountry()"
          ></button>
        </div>
      </template>
    </b-modal>
    <div v-show="countries && countries.length > 0">
      <div class="row justify-content-center">
        <jhi-item-count :page="page" :total="queryCount" :itemsPerPage="itemsPerPage"></jhi-item-count>
      </div>
      <div class="row justify-content-center">
        <b-pagination size="md" :total-rows="totalItems" v-model="page" :per-page="itemsPerPage"></b-pagination>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./country.component.ts"></script>
