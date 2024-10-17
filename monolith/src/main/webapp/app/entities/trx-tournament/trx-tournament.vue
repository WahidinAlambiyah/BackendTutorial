<template>
  <div>
    <h2 id="page-heading" data-cy="TrxTournamentHeading">
      <span v-text="t$('monolithApp.trxTournament.home.title')" id="trx-tournament-heading"></span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" v-on:click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="t$('monolithApp.trxTournament.home.refreshListLabel')"></span>
        </button>
        <router-link :to="{ name: 'TrxTournamentCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-trx-tournament"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="t$('monolithApp.trxTournament.home.createLabel')"></span>
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
              v-bind:placeholder="t$('monolithApp.trxTournament.home.search')"
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
    <div class="alert alert-warning" v-if="!isFetching && trxTournaments && trxTournaments.length === 0">
      <span v-text="t$('monolithApp.trxTournament.home.notFound')"></span>
    </div>
    <div class="table-responsive" v-if="trxTournaments && trxTournaments.length > 0">
      <table class="table table-striped" aria-describedby="trxTournaments">
        <thead>
          <tr>
            <th scope="row" v-on:click="changeOrder('id')">
              <span v-text="t$('global.field.id')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'id'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('name')">
              <span v-text="t$('monolithApp.trxTournament.name')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'name'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('type')">
              <span v-text="t$('monolithApp.trxTournament.type')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'type'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('prizeAmount')">
              <span v-text="t$('monolithApp.trxTournament.prizeAmount')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'prizeAmount'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('startDate')">
              <span v-text="t$('monolithApp.trxTournament.startDate')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'startDate'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('endDate')">
              <span v-text="t$('monolithApp.trxTournament.endDate')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'endDate'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('location')">
              <span v-text="t$('monolithApp.trxTournament.location')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'location'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('maxParticipants')">
              <span v-text="t$('monolithApp.trxTournament.maxParticipants')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'maxParticipants'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('status')">
              <span v-text="t$('monolithApp.trxTournament.status')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'status'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('event.title')">
              <span v-text="t$('monolithApp.trxTournament.event')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'event.title'"></jhi-sort-indicator>
            </th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="trxTournament in trxTournaments" :key="trxTournament.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'TrxTournamentView', params: { trxTournamentId: trxTournament.id } }">{{
                trxTournament.id
              }}</router-link>
            </td>
            <td>{{ trxTournament.name }}</td>
            <td v-text="t$('monolithApp.TournamentType.' + trxTournament.type)"></td>
            <td>{{ trxTournament.prizeAmount }}</td>
            <td>{{ formatDateShort(trxTournament.startDate) || '' }}</td>
            <td>{{ formatDateShort(trxTournament.endDate) || '' }}</td>
            <td>{{ trxTournament.location }}</td>
            <td>{{ trxTournament.maxParticipants }}</td>
            <td v-text="t$('monolithApp.TournamentStatus.' + trxTournament.status)"></td>
            <td>
              <div v-if="trxTournament.event">
                <router-link :to="{ name: 'TrxEventView', params: { trxEventId: trxTournament.event.id } }">{{
                  trxTournament.event.title
                }}</router-link>
              </div>
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link
                  :to="{ name: 'TrxTournamentView', params: { trxTournamentId: trxTournament.id } }"
                  custom
                  v-slot="{ navigate }"
                >
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.view')"></span>
                  </button>
                </router-link>
                <router-link
                  :to="{ name: 'TrxTournamentEdit', params: { trxTournamentId: trxTournament.id } }"
                  custom
                  v-slot="{ navigate }"
                >
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.edit')"></span>
                  </button>
                </router-link>
                <b-button
                  v-on:click="prepareRemove(trxTournament)"
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
          id="monolithApp.trxTournament.delete.question"
          data-cy="trxTournamentDeleteDialogHeading"
          v-text="t$('entity.delete.title')"
        ></span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-trxTournament-heading" v-text="t$('monolithApp.trxTournament.delete.question', { id: removeId })"></p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-text="t$('entity.action.cancel')" v-on:click="closeDialog()"></button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-trxTournament"
            data-cy="entityConfirmDeleteButton"
            v-text="t$('entity.action.delete')"
            v-on:click="removeTrxTournament()"
          ></button>
        </div>
      </template>
    </b-modal>
    <div v-show="trxTournaments && trxTournaments.length > 0">
      <div class="row justify-content-center">
        <jhi-item-count :page="page" :total="queryCount" :itemsPerPage="itemsPerPage"></jhi-item-count>
      </div>
      <div class="row justify-content-center">
        <b-pagination size="md" :total-rows="totalItems" v-model="page" :per-page="itemsPerPage"></b-pagination>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./trx-tournament.component.ts"></script>
