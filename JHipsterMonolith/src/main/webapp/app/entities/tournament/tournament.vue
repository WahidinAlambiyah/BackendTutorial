<template>
  <div>
    <h2 id="page-heading" data-cy="TournamentHeading">
      <span v-text="t$('jHipsterMonolithApp.tournament.home.title')" id="tournament-heading"></span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" v-on:click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="t$('jHipsterMonolithApp.tournament.home.refreshListLabel')"></span>
        </button>
        <router-link :to="{ name: 'TournamentCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-tournament"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="t$('jHipsterMonolithApp.tournament.home.createLabel')"></span>
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
              v-bind:placeholder="t$('jHipsterMonolithApp.tournament.home.search')"
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
    <div class="alert alert-warning" v-if="!isFetching && tournaments && tournaments.length === 0">
      <span v-text="t$('jHipsterMonolithApp.tournament.home.notFound')"></span>
    </div>
    <div class="table-responsive" v-if="tournaments && tournaments.length > 0">
      <table class="table table-striped" aria-describedby="tournaments">
        <thead>
          <tr>
            <th scope="row"><span v-text="t$('global.field.id')"></span></th>
            <th scope="row"><span v-text="t$('jHipsterMonolithApp.tournament.name')"></span></th>
            <th scope="row"><span v-text="t$('jHipsterMonolithApp.tournament.type')"></span></th>
            <th scope="row"><span v-text="t$('jHipsterMonolithApp.tournament.prizeAmount')"></span></th>
            <th scope="row"><span v-text="t$('jHipsterMonolithApp.tournament.startDate')"></span></th>
            <th scope="row"><span v-text="t$('jHipsterMonolithApp.tournament.endDate')"></span></th>
            <th scope="row"><span v-text="t$('jHipsterMonolithApp.tournament.location')"></span></th>
            <th scope="row"><span v-text="t$('jHipsterMonolithApp.tournament.maxParticipants')"></span></th>
            <th scope="row"><span v-text="t$('jHipsterMonolithApp.tournament.status')"></span></th>
            <th scope="row"><span v-text="t$('jHipsterMonolithApp.tournament.event')"></span></th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="tournament in tournaments" :key="tournament.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'TournamentView', params: { tournamentId: tournament.id } }">{{ tournament.id }}</router-link>
            </td>
            <td>{{ tournament.name }}</td>
            <td v-text="t$('jHipsterMonolithApp.TournamentType.' + tournament.type)"></td>
            <td>{{ tournament.prizeAmount }}</td>
            <td>{{ formatDateShort(tournament.startDate) || '' }}</td>
            <td>{{ formatDateShort(tournament.endDate) || '' }}</td>
            <td>{{ tournament.location }}</td>
            <td>{{ tournament.maxParticipants }}</td>
            <td v-text="t$('jHipsterMonolithApp.TournamentStatus.' + tournament.status)"></td>
            <td>
              <div v-if="tournament.event">
                <router-link :to="{ name: 'EventView', params: { eventId: tournament.event.id } }">{{
                  tournament.event.title
                }}</router-link>
              </div>
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'TournamentView', params: { tournamentId: tournament.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.view')"></span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'TournamentEdit', params: { tournamentId: tournament.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.edit')"></span>
                  </button>
                </router-link>
                <b-button
                  v-on:click="prepareRemove(tournament)"
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
          id="jHipsterMonolithApp.tournament.delete.question"
          data-cy="tournamentDeleteDialogHeading"
          v-text="t$('entity.delete.title')"
        ></span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-tournament-heading" v-text="t$('jHipsterMonolithApp.tournament.delete.question', { id: removeId })"></p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-text="t$('entity.action.cancel')" v-on:click="closeDialog()"></button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-tournament"
            data-cy="entityConfirmDeleteButton"
            v-text="t$('entity.action.delete')"
            v-on:click="removeTournament()"
          ></button>
        </div>
      </template>
    </b-modal>
  </div>
</template>

<script lang="ts" src="./tournament.component.ts"></script>
