<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate v-on:submit.prevent="save()">
        <h2
          id="jHipsterMonolithApp.tournament.home.createOrEditLabel"
          data-cy="TournamentCreateUpdateHeading"
          v-text="t$('jHipsterMonolithApp.tournament.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="tournament.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="tournament.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('jHipsterMonolithApp.tournament.name')" for="tournament-name"></label>
            <input
              type="text"
              class="form-control"
              name="name"
              id="tournament-name"
              data-cy="name"
              :class="{ valid: !v$.name.$invalid, invalid: v$.name.$invalid }"
              v-model="v$.name.$model"
              required
            />
            <div v-if="v$.name.$anyDirty && v$.name.$invalid">
              <small class="form-text text-danger" v-for="error of v$.name.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('jHipsterMonolithApp.tournament.type')" for="tournament-type"></label>
            <select
              class="form-control"
              name="type"
              :class="{ valid: !v$.type.$invalid, invalid: v$.type.$invalid }"
              v-model="v$.type.$model"
              id="tournament-type"
              data-cy="type"
            >
              <option
                v-for="tournamentType in tournamentTypeValues"
                :key="tournamentType"
                v-bind:value="tournamentType"
                v-bind:label="t$('jHipsterMonolithApp.TournamentType.' + tournamentType)"
              >
                {{ tournamentType }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label
              class="form-control-label"
              v-text="t$('jHipsterMonolithApp.tournament.prizeAmount')"
              for="tournament-prizeAmount"
            ></label>
            <input
              type="number"
              class="form-control"
              name="prizeAmount"
              id="tournament-prizeAmount"
              data-cy="prizeAmount"
              :class="{ valid: !v$.prizeAmount.$invalid, invalid: v$.prizeAmount.$invalid }"
              v-model.number="v$.prizeAmount.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('jHipsterMonolithApp.tournament.startDate')" for="tournament-startDate"></label>
            <div class="d-flex">
              <input
                id="tournament-startDate"
                data-cy="startDate"
                type="datetime-local"
                class="form-control"
                name="startDate"
                :class="{ valid: !v$.startDate.$invalid, invalid: v$.startDate.$invalid }"
                required
                :value="convertDateTimeFromServer(v$.startDate.$model)"
                @change="updateInstantField('startDate', $event)"
              />
            </div>
            <div v-if="v$.startDate.$anyDirty && v$.startDate.$invalid">
              <small class="form-text text-danger" v-for="error of v$.startDate.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('jHipsterMonolithApp.tournament.endDate')" for="tournament-endDate"></label>
            <div class="d-flex">
              <input
                id="tournament-endDate"
                data-cy="endDate"
                type="datetime-local"
                class="form-control"
                name="endDate"
                :class="{ valid: !v$.endDate.$invalid, invalid: v$.endDate.$invalid }"
                required
                :value="convertDateTimeFromServer(v$.endDate.$model)"
                @change="updateInstantField('endDate', $event)"
              />
            </div>
            <div v-if="v$.endDate.$anyDirty && v$.endDate.$invalid">
              <small class="form-text text-danger" v-for="error of v$.endDate.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('jHipsterMonolithApp.tournament.location')" for="tournament-location"></label>
            <input
              type="text"
              class="form-control"
              name="location"
              id="tournament-location"
              data-cy="location"
              :class="{ valid: !v$.location.$invalid, invalid: v$.location.$invalid }"
              v-model="v$.location.$model"
            />
          </div>
          <div class="form-group">
            <label
              class="form-control-label"
              v-text="t$('jHipsterMonolithApp.tournament.maxParticipants')"
              for="tournament-maxParticipants"
            ></label>
            <input
              type="number"
              class="form-control"
              name="maxParticipants"
              id="tournament-maxParticipants"
              data-cy="maxParticipants"
              :class="{ valid: !v$.maxParticipants.$invalid, invalid: v$.maxParticipants.$invalid }"
              v-model.number="v$.maxParticipants.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('jHipsterMonolithApp.tournament.status')" for="tournament-status"></label>
            <select
              class="form-control"
              name="status"
              :class="{ valid: !v$.status.$invalid, invalid: v$.status.$invalid }"
              v-model="v$.status.$model"
              id="tournament-status"
              data-cy="status"
            >
              <option
                v-for="tournamentStatus in tournamentStatusValues"
                :key="tournamentStatus"
                v-bind:value="tournamentStatus"
                v-bind:label="t$('jHipsterMonolithApp.TournamentStatus.' + tournamentStatus)"
              >
                {{ tournamentStatus }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('jHipsterMonolithApp.tournament.event')" for="tournament-event"></label>
            <select class="form-control" id="tournament-event" data-cy="event" name="event" v-model="tournament.event">
              <option v-bind:value="null"></option>
              <option
                v-bind:value="tournament.event && eventOption.id === tournament.event.id ? tournament.event : eventOption"
                v-for="eventOption in events"
                :key="eventOption.id"
              >
                {{ eventOption.title }}
              </option>
            </select>
          </div>
        </div>
        <div>
          <button type="button" id="cancel-save" data-cy="entityCreateCancelButton" class="btn btn-secondary" v-on:click="previousState()">
            <font-awesome-icon icon="ban"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.cancel')"></span>
          </button>
          <button
            type="submit"
            id="save-entity"
            data-cy="entityCreateSaveButton"
            :disabled="v$.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.save')"></span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./tournament-update.component.ts"></script>
