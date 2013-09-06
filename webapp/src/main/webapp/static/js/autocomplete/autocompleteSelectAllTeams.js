
/**
 * Selection of all teams
 * <p>
 * @constructor
 */
var AutocompleteSelectAllTeams = function AutocompleteSelectAllTeams(element, dataType, parent, opts) {
  this.recentlySelected = [];
  this.element = element;
  this.parent = parent;
  this.options = {
      caption: "Select all teams",
  };
};

/**
 * Initialize the multiple teams box.
 */
AutocompleteSelectAllTeams.prototype.initialize = function() {
};

/**
 * Render the list.
 */
AutocompleteSelectAllTeams.prototype.render = function() {
  var me = this;
  
  this.caption = $('<span/>').text(this.options.caption).appendTo(this.element);
  var select = function() {
    /*var allTeams = this.parent.getAllTeams();
    $.each(allTeams, function(k,v) {
    	me.parent.selectItem(v);
    });*/
    alert("select");
  };
  this.caption.click(select);
  /*
  this.caption = $('<span/>').text(this.options.caption).appendTo(this.element);
  this.suggestionList = $('<ul/>').addClass(AutocompleteVars.cssClasses.recentList);
  this.element.append(this.suggestionList);
  var items = this.parent.getItemsByIdList(this.recentlySelected);
  
  $.each(items, function(k,v) {
    var select = function() { me.parent.selectItem(v); };
    var listItem = $('<li/>').text(v.name).attr('title',v.name).appendTo(me.suggestionList);
    listItem.click(select);
  });
  */
};


